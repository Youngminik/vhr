<template>
  <div>
    <div>
      <el-input
          v-loading="loading"
          element-loading-text="正在加载中..."
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          size="small" class="addPosInput"
          placeholder="添加职位..."
          prefix-icon="el-icon-plus"
          @keydown.enter.native="addPosition"
          v-model="pos.name">
      </el-input>
      <el-button icon="el-icon-plus" size="mini" type="primary"
                 @click="addPosition">添加
      </el-button>
    </div>
    <div class="posManaMain">
      <!--@selection-change="handleSelectionChange"：
      点击多选框会触发的点击事件-->
      <el-table
          :data="positions"
          border
          @selection-change="handleSelectionChange"
          size="small"
          stripe
          style="width: 70%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column
            prop="id"
            label="编号"
            width="55">
        </el-table-column>
        <el-table-column
            prop="name"
            label="职位名称"
            width="180">
        </el-table-column>
        <el-table-column
            prop="createDate"
            width="120"
            label="创建时间">
        </el-table-column>
        <el-table-column
            label="是否启用">
          <template slot-scope="scope">
            <el-tag size="small" type="success" v-if="scope.row.enabled">已启用</el-tag>
            <el-tag size="small" type="danger" v-else>未启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column width="180" label="操作">
          <template slot-scope="scope">
            <!--index当前操作到第几行，row：当前行返回的json对象-->
            <el-button
                size="mini"
                @click="showEditView(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--disabled:表示禁用当前这个按钮
      multipleSelection是handleSelectionChange的val值，表示有几个选项
      multipleSelection==0：一个都没选是禁用状态
      disabled前面加冒号： 使得引用的内容是个表达式，防止disabled将引用的内容当成字符串来处理-->
      <el-button @click="deleteMany" type="danger" size="small" style="margin-top: 8px"
                 :disabled="multipleSelection==0">批量删除
      </el-button>
    </div>
    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="30%">
      <div>
        <el-tag>职位名称</el-tag>
        <el-input class="updatePosInput" size="small" v-model="updatePos.name"></el-input>
      </div>
      <div>
        <el-tag>是否启用</el-tag>
        <el-switch
            v-model="updatePos.enabled"
            active-text="启用"
            inactive-text="禁用">
        </el-switch>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false" size="small">取 消</el-button>
    <el-button type="primary" @click="doUpdate" size="small">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "PosMana",
  data() {
    return {
      pos: {
        name: ''
      },
      /*对应编辑对话框中的属性，默认情况下是关闭的*/
      dialogVisible: false,
      /*弹出编辑框时，对应的职位名称data能够显示在对话框中*/
      updatePos: {
        name: '',
        enabled: false
      },
      multipleSelection: [],
      /*存放从数据库传来的数据，注意！el-table-column中的prop的字段要跟数据库中的一样，否则拿不到数据*/
      positions: [],
      loading: false
    }
  },
  /*当页面加载完成后，对应的initPositions()方法也被调用*/
  mounted() {
    this.initPositions();
  },
  methods: {
    /*添加职位*/
    addPosition() {
      if (this.pos.name) {
        /*this.postRequest:相当于测试接口发送一个post请求*/
        this.postRequest("/system/basic/pos/", this.pos).then(resp => {
          if (resp) {
            /*触发添加操作之后，刷新页面*/
            this.initPositions();
            /*添加之后，添加输入框清空*/
            this.pos.name = '';
          }
        });
      } else {
        this.$message.error("职位名称不能为空");
      }
    },
    /*删除职位*/
    handleDelete(index, data) {
      this.$confirm('此操作将永久删除【' + data.name + '】职位, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        /*this.deleteRequest:相当于测试接口发送一个delete请求*/
        this.deleteRequest("/system/basic/pos/" + data.id).then(resp => {
          if (resp) {
            this.initPositions();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    /* initPositions:有增删改查的操作之后会刷新表格*/
    initPositions() {
      this.loading = true;
      this.getRequest("/system/basic/pos/").then(resp => {
            this.loading = false;
            if (resp) {
              this.positions = resp;
            }
          }
      )
    },
    /*编辑操作*/
    showEditView(index, data) {
      /*防止在编辑框编辑时，在表格中的数据跟着改变，同时，点击取消时，不会改变原先的数据*/
      Object.assign(this.updatePos, data);
      /*点击编辑框时，dialogTableVisible属性为true，弹出编辑框，开启编辑模式*/
      this.dialogVisible = true;
    },
    /*编辑框中修改职位之后的确定按钮*/
    doUpdate() {
      /*put：向后端发送一个修改数据的请求*/
      this.putRequest("/system/basic/pos/", this.updatePos).then(resp => {
        /*请求成功之后的一系列操作*/
        if (resp) {
          /*刷新页面*/
          this.initPositions();
          /*清空编辑对话框上的文字*/
          this.updatePos.name = '';
          /*关闭编辑框*/
          this.dialogVisible = false;
        }
      });
    },
    /*多选框的点击事件*/
    handleSelectionChange(val) {
      /*保存选中的项*/
      this.multipleSelection = val;
    },
    //  批量删除的点击事件
    deleteMany() {
      this.$confirm('此操作将永久删除【' + this.multipleSelection.length + '】条记录, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        /*在遍历multipleSelection中取得每一个id，将其拼接起来*/
        let ids = '?';
        this.multipleSelection.forEach(item => {
          ids += "ids=" + item.id + '&';
        })
        this.deleteRequest("/system/basic/pos/" + ids).then(resp => {
          if (resp) {
            this.initPositions();
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
/*添加职位输入框*/
.addPosInput {
  width: 300px;
  margin-right: 8px
}

/*职位管理表格*/
.posManaMain {
  margin-top: 10px;
}

/*编辑职位框*/
.updatePosInput {
  width: 200px;
  margin-left: 8px;
}
</style>