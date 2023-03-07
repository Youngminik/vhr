<template>
  <div class="main">
    <el-container>
      <el-header class="homeHeader">
        <div class="title"> 微人事</div>
        <div>
          <el-button icon="el-icon-bell" type="text" style="margin-right: 8px;color: darkgreen" size="normal"
          @click="goChat"></el-button>
          <el-dropdown class="userInfo" @command="commandHandler">
             <span class="el-dropdown-link">
          <div> {{ user.name }}</div>
            <div> <el-avatar size="11" img :src="user.userface" style="margin-left: 0"  alt=""></el-avatar></div>
            </span>
<!--            <span class="el-dropdown-link">
    {{user.name}}<i><img :src="user.userface" style="margin-left: 0" alt=""></i>
  </span>-->
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="setting">设置</el-dropdown-item>
              <el-dropdown-item divided command="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <el-aside width="200px" style="background-color: #ffffff;
        border-top:1px solid #DDDDDD">
          <el-menu router unique-opened background-color="#ffffff">
            <el-submenu :index="index" v-for="(item,index) in  routes" v-if="!item.hidden"
                        :key="index">
              <template slot="title">
                <i style="color: #505458;margin-right: 5px" :class="item.iconCls"></i>
                <span>{{ item.name }}</span>
              </template>
              <el-menu-item :index="child.path" v-for="(child,indexj) in item.children"
                            :key="indexj">{{ child.name }}
              </el-menu-item>
            </el-submenu>
          </el-menu>

        </el-aside>
        <el-main class="el-main">
          <el-breadcrumb separator-class="el-icon-arrow-right" v-if="this.$router.currentRoute.path!='/home'">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ this.$router.currentRoute.name }}</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="homeWelcome" v-if="this.$router.currentRoute.path=='/home'">
            欢迎来到微人事！
          </div>
          <router-view class="homeRouterView"/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Home.vue",
  data() {
    return {
      user: JSON.parse(window.sessionStorage.getItem("user"))
    }
  },
  computed: {
    routes() {
      return this.$store.state.routes;
    }
  },
  methods: {
    goChat(){
      this.$router.push("/chat");
    },
    menuClick(index, indexPath) {
      this.$router.push(index)
    },
    commandHandler(cmd) {
      /*注销登录*/
      if (cmd == 'logout') {
        this.$confirm('您将要退出登录，是否继续？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          /* 清空session*/
          this.getRequest("/logout");
          window.sessionStorage.removeItem("user");
          this.$store.commit('initRoutes', []);
          this.$router.replace('/');
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消退出登录'
          });
        });
      }

    }
  }
}
</script>

<style>

.main {
  background-color: #F7F7F7;
  margin-bottom: 0px;

}

.homeHeader {
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 0 2px #AAAAAA;
  border-bottom-width: thin;
  /*  box-sizing: border-box;
    border: 1px solid #cac6c6;
    box-shadow: 0 0 8px #cac6c6;*/
}

.title {
  font-size: 30px;
  font-family: 华文新魏;
  padding: 15px;
  color: #04356c;
}

.userInfo {
  cursor: pointer;
}

.el-dropdown-link img {
  width: 48px;
  height: 40px;
  margin-left: 8px;

}

.el-dropdown-link {
  display: flex;
  align-items: center;
  font-size: 19px;
  font-family: 华文行楷;
  color: #274F7D;

}

/*欢迎页面字体*/
.homeWelcome {
  text-align: center;
  font-family: 华文新魏;
  font-size: 30px;
  color: cornflowerblue;
  padding-top: 40px;
}

/*表格属性*/
.homeRouterView {
  margin-top: 10px;
}


</style>