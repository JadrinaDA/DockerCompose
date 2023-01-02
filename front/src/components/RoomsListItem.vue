<template>
  <div class="room border border-secondary rounded p-2 mb-2" :class="{expanded: isExpanded}">
    <div class="top-row d-flex" @click="toggleExpand">
      <div class="room-name fw-bold pe-3">{{room.name}}</div>
      <div class="room-temperature text-muted">{{room.targetTemperature}}</div>
      <div class="expand-button ms-auto">
        {{ isExpanded ? '&#9660;' : '&#9658;' }}
      </div>
    </div>
    <template v-if="isExpanded">
      <hr/>
      <div class="details d-flex">
        <input type="number" v-model = "temp"><br><br>
        <button type="button" class="btn btn-secondary me-2"  @click="switchRoom"> Change Target Temperature </button>
        <button type="button" class="btn btn-danger" @click="deleteRoom">Delete Room</button>
      </div>
    </template>
  </div>
</template>

<script>

import axios from 'axios';
import {API_HOST} from '../config';
 export default{
     name: 'RoomsListItem',
     props: ['room'],
     data: function (){
         return {
            isExpanded: false,
            temp: 0
        }
    },
     methods: {
         toggleExpand() {
             this.isExpanded = !this.isExpanded;
         },
         async switchRoom() {
             this.room.targetTemperature = this.temp;
             let response = await axios.post(`${API_HOST}/api/rooms/`, this.room);
             let updateRoom = response.data;
             this.$emit('room-update', updateRoom);
         },

         async deleteRoom(){
             let response = await axios.delete(`${API_HOST}/api/rooms/${this.room.id}`);
             this.$emit('room-deleted');
         }

     }
 }

</script> 