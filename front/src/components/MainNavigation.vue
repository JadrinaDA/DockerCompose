<template>
  <div>
    <div v-if="activeTab == 'windows'">
      <ul class="nav nav-tabs">
        <li class="nav-item" @click="activeTab = 'windows'">
          <a class="nav-link active" href="#">Windows</a>
        </li>
        <li class="nav-item" @click="activeTab = 'rooms'">
            <a class="nav-link" href="#">Rooms</a>
        </li>
      </ul>
      <windows-list></windows-list>
    </div>
    <div v-if="activeTab == 'rooms'">
      <ul class="nav nav-tabs">
        <li class="nav-item" @click="activeTab = 'windows'">
          <a class="nav-link" href="#">Windows</a>
        </li>
        <li class="nav-item" @click="activeTab = 'rooms'">
            <a class="nav-link active" href="#">Rooms</a>
        </li>
      </ul>
      <rooms-list></rooms-list>
    </div>
  </div>

</template>


<script>
import WindowsList from './WindowsList.vue';
import RoomsList from './RoomsList.vue';
export default {
  name : 'MainNavigation',
  components : {
    RoomsList,
    WindowsList,
    
  },
  data : function () {
    return {
      activeTab: "windows"
    }
  }
}
</script>