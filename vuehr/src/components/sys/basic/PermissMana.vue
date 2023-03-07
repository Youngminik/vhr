<template>
  <div
      v-loading="globalLoading"
      element-loading-text="正在加载中..."
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)">
    <div class="permissManaTool">
      <el-input placeholder="请输入角色英文名" v-model="role.name">
        <template slot="prepend">ROLE_</template>
      </el-input>
      <el-input placeholder="请输入角色中文名" v-model="role.nameZh" @keydown.enter.native="doAddRole"></el-input>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="doAddRole">添加角色</el-button>
    </div>
    <div class="permissMana">
      <!--activeName:对应下边每一项的name属性，name是哪个值，就打开哪一项-->
      <el-collapse
          v-loading="loading"
          element-loading-text="正在加载中..."
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          v-model="activeName"
          accordion @change="change">
        <el-collapse-item :title="r.nameZh" :name="r.id" v-for="(r,index) in roles" :key="index">
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>可访问的资源</span>
              <el-button style="float: right; padding: 3px 0;color:#ff0000" type="text"
                         icon="el-icon-delete" @click="deleteRole(r)"></el-button>
            </div>
            <div>
              <!--key:index是为了防止在点击一个面板之后去点击另一个面板出现延迟，从而导致下一个面板的返回值为空-->
              <el-tree show-checkbox
                       node-key="id"
                       ref="tree"
                       :default-checked-keys="selectedMenus"
                       :data="allmenus"
                       :props="defaultProps"
                       :key="index"
              ></el-tree>
              <div style="display: flex;justify-content: flex-end">
                <el-button @click="cancelUpdate">取消修改</el-button>
                <el-button type="primary" @click="doUpdate(r.id,index)">确认修改</el-button>
              </div>
            </div>
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import {initMenu} from "../../../utils/menus";

export default {
  name: "PermissMana",
  data() {
    return {
      role: {
        name: '',
        nameZh: ''
      },
      activeName: -1,
      roles: [],
      allmenus: [],
      /*defaultProps中的字段key-value值
      * key可以随意命名，value跟数据库中的字段一样
      * 如果数据库中的字段同为children和label
      * 不用定义defaultProps*/
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      /*被选中的menu项*/
      selectedMenus: [],
      loading:false,
      globalLoading:false
    }
  },
  /*调用方法*/
  mounted() {
    this.initRoles();
  },
  methods: {
    initRoles() {
      this.loading=true;
      this.getRequest("/system/basic/permiss/").then(resp => {
      this.loading=false;
        if (resp) {
          this.roles = resp;
        }
      })
    },
    change(rid) {
      if (rid) {
        this.initAllMenus();
        this.initSelectedMenus(rid);
      }
    },
    initAllMenus() {
      this.getRequest("/system/basic/permiss/menus").then(resp => {
        if (resp) {
          this.allmenus = resp;
        }
      })
    },
    initSelectedMenus(rid) {
      this.getRequest("/system/basic/permiss/mids/" + rid).then(resp => {
        if (resp) {
          this.selectedMenus = resp;
        }
      })
    },
    /*确认修改,更新操作*/
    doUpdate(rid, index) {
      let tree = this.$refs.tree[index];
      /*getCheckedKeys:只返回叶子结点的值
      * 即只取到二级菜单的值，不用取没有的一级菜单的值*/
      let selectedKeys = tree.getCheckedKeys(true);
      let url = '/system/basic/permiss/?rid=' + rid;
      selectedKeys.forEach(key => {
        url += '&mids=' + key;
      })
      this.putRequest(url).then(resp => {
        if (resp) {
          /*更新成功，不需要刷新页面，只需要关闭面板即可*/
          /*关闭折叠面板*/
          this.activeName = -1;
        }
      })
    },
    /*取消更新操作*/
    cancelUpdate() {
      this.activeName = -1;
    },
    /*添加角色*/
    doAddRole() {
      /*输入框不为空才进行添加操作*/
      if (this.role.name && this.role.nameZh) {
        this.globalLoading=true;
        this.postRequest("/system/basic/permiss/role", this.role).then(resp => {
        this.globalLoading=false;
          if (resp) {
            /*清空输入框的文字*/
            this.role.name = '';
            this.role.nameZh = '';
            this.initRoles();
          }
        })
      } else {
        this.$message.error("数据不能为空！");
      }
    },
    /*删除*/
    deleteRole(role) {
      this.$confirm('此操作将永久删除【' + role.nameZh + '】角色, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRequest("/system/basic/permiss/role/" + role.id).then(resp => {
          if (resp) {
            this.initRoles();
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
/*顶部输入框*/
.permissManaTool .el-input {
  width: 300px;
  margin-right: 8px;
}

.permissMana {
  margin-top: 15px;
  width: 700px
}
</style>