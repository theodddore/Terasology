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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.inventory.events.DropItemEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.physics.HitResult;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.physics.components.RigidBodyComponent;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.engine.PhysicsEngine;
import org.terasology.physics.engine.RigidBody;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

import java.util.Iterator;
import java.util.List;


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

                        entity.addComponent(new BurnableItemComponent());
                        if (entity.hasComponent(BurnableItemComponent.class)) {

                            entity.send(new onLavaEnterEvent(entity));

                        }
                    }
                }
        }
    }

    /**
     * droppedInLava is the method invoked when an item fall into a lavaBlock.
     * @param event .
     * @param entity the lava entity in which the item falls into.
     */
    @ReceiveEvent
    public void droppedInLava(onLavaEnterEvent  event, EntityRef entity) {

            event.getItem().addComponent(new DestroyedItemComponent());

    }



}
