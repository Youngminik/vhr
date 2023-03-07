<template>
  <div>
    <div style="margin-top:10px">
      <el-input v-model="keywords" placeholder="通过用户名搜索用户..." size="small"
                prefix-icon="el-icon-search " style="width: 400px;margin-right: 10px"
                @keydown.enter.native="doSearch"></el-input>
      <el-button icon="el-icon-search" type="primary" @click="doSearch">搜索</el-button>
    </div>
    <div class="hr-container">
      <el-card class="hr-card" v-for="(hr,index) in hrs" :key="index">
        <div slot="header" class="clearfix">
          <span>{{ hr.name }}</span>
          <el-button style="float: right; padding: 3px 0;color: red" type="text"
                     icon="el-icon-delete" @click="deleteHr(hr)"></el-button>
        </div>
        <div>
          <div class="img-container">
            <img :src="hr.userface" :alt="hr.name" :title="hr.name" class="userface-img">
          </div>
          <!--show:当显示出添加用户角色面板时，触发该事件-->
          <div class="userinfo-container">
            <div>用户名：{{ hr.name }}</div>
            <div>手机号码：{{ hr.phone }}</div>
            <div>电话号码：{{ hr.telephone }}</div>
            <div>地址：{{ hr.address }}</div>
            <div>用户状态：
              <el-switch
                  v-model="hr.enabled"
                  @change="enabledChange(hr)"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                  active-text="启用"
                  inactive-text="禁用">
              </el-switch>
            </div>
            <div>用户角色：
              <el-tag type="success" style="margin-right:2px"
                      v-for="(role,indexj) in hr.roles" :key="indexj">{{ role.nameZh }}
              </el-tag>
              <!--multiple:多选属性
              hidePop:当添加新角色后收起面板时，进行更新页面的操作-->
              <el-popover
                  placement="right"
                  title="角色列表"
                  width="200"
                  trigger="click"
                  @show="showPop(hr)"
                  @hide="hidePop(hr)">
                <el-select v-model="selectedRoles" multiple placeholder="请选择">
                  <el-option
                      v-for="(r,indexk) in allroles"
                      :key="indexk"
                      :label="r.nameZh"
                      :value="r.id">
                  </el-option>
                </el-select>
                <el-button slot="reference" icon="el-icon-more" type="text"></el-button>
              </el-popover>
            </div>
            <div>备注：{{ hr.remark }}</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "SysHr.vue",
  data() {
    return {
      keywords: '',
      hrs: [],
      allroles: [],
      selectedRoles: []
    }
  },
  mounted() {
    this.initHrs();
  },
  methods: {
    initHrs() {
      this.getRequest("/system/hr/?keywords=" + this.keywords).then(resp => {
        if (resp) {
          this.hrs = resp;
        }
      })
    },
    initRoles() {
      this.getRequest("/system/hr/roles").then(resp => {
        if (resp) {
          this.allroles = resp;
        }
      })
    },
    /*更新*/
    enabledChange(hr) {
      delete hr.roles;
      this.putRequest("/system/hr/", hr).then(resp => {
        this.initHrs();
      })
    },
    /*展示面板时的事件*/
    showPop(hr) {
      this.initRoles();
      let roles = hr.roles;
      /*清空历史记录*/
      this.selectedRoles = [];
      roles.forEach(r => {
        this.selectedRoles.push(r.id);
      })
    },
    /*隐藏面板进行更新操作
    * roles:获取数据库中的roles
    * flag:用来判断当前是否需要进行更新操作
    * 如果roles跟selectedRoles的长度不一样，则进行更改
    * 如果两个长度不一样，如果在roles中有selected存放的id*/
    hidePop(hr) {
      /*将数据库中的hr.roles拷贝成一个新数组*/
      let roles = [];
      Object.assign(roles, hr.roles);
      let flag = false;
      if (roles.length != this.selectedRoles.length) {
        flag = true;
      } else {
        for (let i = 0; i < roles.length; i++) {
          let role = roles[i];
          for (let j = 0; j < this.selectedRoles.length; j++) {
            let sr = this.selectedRoles[j];
            if (role.id == sr) {
              roles.splice(i, 1);
              i--;
              break;
            }
          }
        }
        if (roles.length != 0) {
          flag = true;
        }
      }
      if (flag) {
        let url = '/system/hr/role?hrid=' + hr.id;
        this.selectedRoles.forEach(sr => {
          url += '&rids=' + sr;
        })
        this.putRequest(url).then(resp => {
          if (resp) {
            this.initHrs();
          }
        })
      }
    },
    /*搜索框*/
    doSearch() {
      this.initHrs();
    },
    /*删除操作*/
    deleteHr(hr) {
      this.$confirm('此操作将永久删除【'+hr.name+'】, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRequest("/system/hr/"+hr.id).then(resp=>{
          if(resp){
            this.initHrs();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  }
}
</script>

<style>
.hr-card {
  width: 350px;
  margin-bottom: 20px;
  margin-left: 35px;
}

.hr-container {
  margin-top: 10px;
  display: flex;
  /*自动换行*/
  flex-wrap: wrap;
}

/*头像*/
.userface-img {
  width: 72px;
  height: 72px;
  border-radius: 72px;
}

.img-container {
  width: 100%;
  display: flex;
  justify-content: center;
}

/*文本框*/
.userinfo-container {
  margin-top: 20px;
}

.userinfo-container div {
  font-size: 12px;
  color: #1A94E6;
}
</style>