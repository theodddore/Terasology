/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.lavatest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.physics.HitResult;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.physics.components.RigidBodyComponent;
import org.terasology.physics.engine.PhysicsEngine;
import org.terasology.physics.engine.RigidBody;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;

import java.util.Iterator;

/**@Authors Thodoris Mavrikis, Alex Stellas **/
@RegisterSystem(RegisterMode.AUTHORITY)
public class LavaDestructionSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(LavaDestructionSystem.class);

    @In
    private BlockEntityRegistry blockEntityProvider;

    @In
    private Time time;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    @In
    private PhysicsEngine physics;


    /**
     * Update is the implementation of the UpdateSubscriber Interface. It contains the
     * core code of the module. It implements two core functions, firstly it sends events
     * when objects hit lava and secondly it destroys items that have DestroyedItemComponent
     * component.
     * @param delta The time (in seconds) since the last engine update.
     */
    @Override
    public void update(float delta){

        for (EntityRef e : entityManager.getEntitiesWith(DestroyedItemComponent.class)) {
            e.destroy();
        }

        PerformanceMonitor.startActivity("Physics Renderer");
        physics.update(time.getGameDelta());
        PerformanceMonitor.endActivity();

        Iterator<EntityRef> iter = physics.physicsEntitiesIterator();

        while (iter.hasNext()) {
                EntityRef entity = iter.next();
                RigidBodyComponent comp = entity.getComponent(RigidBodyComponent.class);
                RigidBody body = physics.getRigidBody(entity);

                if (body.isActive()) {
                    body.getLinearVelocity(comp.velocity);
                    body.getAngularVelocity(comp.angularVelocity);

                    Vector3f location = Vector3f.zero();
                    body.getLocation(location);
                    HitResult hitResult = physics.rayTrace(location, comp.velocity.normalize(), 0.2f, StandardCollisionGroup.LIQUID);
                    if (hitResult.isHit() == true) {

                        if (entity.hasComponent(BurnableItemComponent.class)) {

                            if (hitResult.getEntity().hasComponent(BlockComponent.class)) {

                                BlockComponent liquidBlockComp = hitResult.getEntity().getComponent(BlockComponent.class);
                                Block liquidBlock = liquidBlockComp.getBlock();

                                if (liquidBlock.isLava()) {

                                    entity.send(new onLavaEnterEvent(entity));
                                }
                            }
                        }
                    }
                }
        }
    }

    /**
     * onDropItemEvent is the method invoked when a character drops an item.
     * It adds the BurnableItemComponent to the dropped item,
     * in order to know that they can be burned(destroyed)
     * @param event DropItemEvent that are send when an item is dropped.
     * @param itemEntity the entity of the item that is dropped.
     */
    @ReceiveEvent
    public void onDropItemEvent(DropItemEvent event, EntityRef itemEntity) {
        itemEntity.addComponent(new BurnableItemComponent());
    }

    /**
     * droppedInLava is the method invoked when an item fall into a lavaBlock.
     * @param event onLavaEnterEvent that are send when an object hits lava.
     * @param entity the lava entity in which the item falls into.
     */
    @ReceiveEvent
    public void droppedInLava(onLavaEnterEvent  event, EntityRef entity) {
        event.getItem().addComponent(new DestroyedItemComponent());
    }



}
