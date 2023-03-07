<template>
  <div style="width: 500px;">
    <el-input
        placeholder="请输入部门名称进行搜索..."
        prefix-icon="el-icon-search"
        v-model="filterText">
    </el-input>
    <!--slot-scope="{ node, data }":
    node:每一项元素
    data：每一项元素的数据
    -->
    <el-tree
        :expand-on-click-node="false"
        :data="deps"
        :props="defaultProps"
        :filter-node-method="filterNode"
        ref="tree">
     <span class="custom-tree-node" slot-scope="{ node, data }"
           style="justify-content: space-between;display: flex;width: 100%">
<!--        <span>{{ node.label }}</span>-->
       <span>{{data.name}}</span>
        <span>
          <el-button
              class="depBtn"
              type="primary"
              size="mini"
              @click="() => showAddDepView(data)">
            添加部门
          </el-button>
          <el-button
              class="depBtn"
              type="danger"
              size="mini"
              @click="() => deleteDep(data)">
            删除部门
          </el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog
        title="添加部门"
        :visible.sync="dialogVisible"
        width="30%">
      <div>
        <table>
          <tr>
            <td>
              <el-tag>上级部门</el-tag>
            </td>
            <td>{{ pname }}</td>
          </tr>
          <tr>
            <td>
              <el-tag>部门名称</el-tag>
            </td>
            <td>
              <el-input v-model="dep.name" placeholder="请输入部门名称..."></el-input>
            </td>
          </tr>
        </table>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="doAddDep">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "DepMana",
  data() {
    return {
      filterText: '',
      deps: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      dialogVisible: false,
      dep: {
        name: '',
        parentId: -1
      },
      pname: ''
    }
  },
  /*监控器:当filterText的值发生变化时，会触发该方法，val是filterText的最新值*/
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {
    this.initDeps();
  },
  methods: {
    /*value：filterText中的value，data：传进来的json数组中的每一项*/
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    /*加载部门数据*/
    initDeps() {
      this.getRequest("/system/basic/department/").then(resp => {
        if (resp) {
          this.deps = resp;
        }
      })
    },
    /*添加部门*/
    showAddDepView(data) {
      this.pname = data.name;
      this.dep.parentId = data.id;

      this.dialogVisible = true;
    },
    /*删除部门*/
    deleteDep(data) {
      /*如果是父部门，显示删除失败*/
      if (data.parent ) {
        this.$message.error("父部门删除失败！");
      } else {
        this.$confirm('此操作将永久删除【' + data.name + '】部门, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {

          this.deleteRequest("/system/basic/department/" + data.id).then(resp => {
            if (resp) {
              this.removeDepFromDeps(null,this.deps, data.id);

            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      }
    },
    /*在删除功能中用于移除某个菜单项*/
    removeDepFromDeps(p,deps, id) {
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        /*deps是整个树， d表示第几个子树
        * d.id表示该json数组中的id是否等于要删除的id*/
        if (d.id == id) {
          /*从i开始，移除一项*/
          deps.splice(i, 1);
          if(deps.length==0){
            p.parent=false;
          }
          return;
        } else {
          this.removeDepFromDeps(d,d.children, id);
        }
      }
    },
    /*添加部门中的确定按钮*/
    doAddDep() {
      this.postRequest("/system/basic/department/", this.dep).then(resp => {
        if (resp) {
          this.addDep2Deps( this.deps, resp.obj)
          /*收起弹出框*/
          this.dialogVisible = false;
          /*初始化变量*/
          this.initDep();
        }
      })
    },
    addDep2Deps( deps, dep) {
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        if (d.id = dep.parentId) {
          d.children = d.children.concat(dep);
          if(d.children.length>0){
            d.parent=true;
          }
          return;
        } else {
          this.addDep2Deps( d.children, dep);
        }
      }
    },
    initDep() {
      this.dep = {
        name: '',
        parentId: -1
      }
      this.pname = '';
    }
  }
}
</script>

<style>
.depBtn {
  padding: 2px;
}
</style>