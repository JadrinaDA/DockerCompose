<template>
  <!-- based off https://serversideup.net/post-put-patch-requests-with-vuejs-and-axios/ -->
  <div class="window border border-secondary rounded p-2 mb-2">
      <label for="name">Name:</label><br>
      <input type="text" v-model = "form.name"><br>
      <label for="status">Window Status:</label><br>
      <select class="form-control" v-model = "form.windowStatus">
        <option>OPEN</option>
        <option>CLOSED</option>
      </select>
      <label for="rname">Room Name:</label><br>
      <input type="text" v-model = "form.roomName"><br>
      <label for="rid">Room Id:</label><br>
      <input type="number" v-model = "form.roomId"><br><br>
      <button v-on:click="createWindow()">Submit</button>
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
  name: 'WindowForm',
  data(){
        return {
            errors: [],
            form: {
                name: '',
                windowStatus: '',
                roomName: '',
                roomId: 0
            }
        }
    },
  methods: {
    async createWindow() {
      this.errors = [];
      if (!this.form.name) {
      this.errors.push('Window name required');
      }
      if (!this.form.windowStatus) {
      this.errors.push('Window status required');
      }
      if (!this.form.roomId) {
      this.errors.push('Room ID required');
      }
      if (this.errors.length == 0)
      {
        let response = await axios.post(`${API_HOST}/api/windows`, this.form);
        let newWindow = response.data;
        this.$emit('window-created', newWindow);
      }
    }
  }
}
</script>
