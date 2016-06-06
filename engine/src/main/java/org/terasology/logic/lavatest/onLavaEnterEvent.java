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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;


/**
 * @authors Thodoris Mavrikis, Alex Stellas
 * This class represents the event of
 * an item entering into a lava block
 */
public class onLavaEnterEvent implements Event {

  private EntityRef item;

  /**
   * Constructor of the event, initializes
   * the item that hit the lava block.
   * @param item, the item which entered lava
   */
  public onLavaEnterEvent(EntityRef item){

    this.item = item;

  }

    /**
     * Setter method for the item which entered lava.
     * @param item, the item which entered in lava.
     */
  public void setItem(EntityRef item) {
    this.item = item;
  }

    /**
     * Getter method for the item which entered lava.
     * @return the item which entered in the lava.
     */
  public EntityRef getItem() {
    return item;
  }


}
