<template>
  <!-- based off https://serversideup.net/post-put-patch-requests-with-vuejs-and-axios/ -->
  <div class="room border border-secondary rounded p-2 mb-2">
      <label for="name">Name:</label><br>
      <input type="text" v-model = "form.name"><br>
      <label for="bid">Building Id:</label><br>
      <input type="number" v-model = "form.buildingId"><br><br>
       <label for="floor">Floor:</label><br>
      <input type="number" v-model = "form.floor"><br><br>
      <button v-on:click="createRoom()">Submit</button>
      <div v-if="errors.length > 0">
       <span v-for="error in errors" :key="error">
        {{error}},
       </span>
      </div>
  </div>
</template>

<script>
import axios from 'axios';
import {API_HOST} from '../config';

export default {
  name: 'RoomForm',
  data(){
        return {
            errors: [],
            form: {
                name: '',
                buildingId: 0,
                floor: 0
            }
        }
    },
  methods: {
    async createRoom() {
      this.errors = [];
      if (!this.form.name) {
      this.errors.push('Room name required');
      }
      if (!this.form.buildingId) {
      this.errors.push('Building ID required');
      }
      if (!this.form.floor) {
      this.errors.push('Floor required');
      }
      if (this.errors.length == 0)
      {
        let response = await axios.post(`${API_HOST}/api/rooms`, this.form);
        let newRoom = response.data;
        this.$emit('room-created', newRoom);
      }
    }
  }
}
</script>
