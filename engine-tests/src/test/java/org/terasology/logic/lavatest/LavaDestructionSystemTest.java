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

import org.junit.Assert;
import org.junit.Test;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.location.LocationComponent;
import org.terasology.protobuf.EntityData;
import org.terasology.registry.In;
import org.mockito.Mockito;
import sun.security.krb5.internal.crypto.Des;

import static org.mockito.Mockito.mock;

/**
 * Created by Theodore Mavrikis on 4/6/2016.
 */
public class LavaDestructionSystemTest {


    @In
    private EntityManager entityManager;


    @Test
    public void testEvent() {

        /** Set up **/
        DestroyedItemComponent dest = new DestroyedItemComponent();

        onLavaEnterEvent ev;

        EntityRef entity = mock(EntityRef.class);

        ev = new onLavaEnterEvent(entity);

        entity.send(ev);

        Mockito.when(ev.getItem().getComponent(DestroyedItemComponent.class)).thenReturn(dest);


        /** Testing **/

        Assert.assertNotNull(entity.getComponent(DestroyedItemComponent.class));

    }


}
