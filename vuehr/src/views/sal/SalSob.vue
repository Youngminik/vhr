<template>
  <div>
    <div style="display: flex;justify-content: space-between">
      <el-button icon="el-icon-plus" type="primary" @click="showAddSalaryView">添加工资帐套管理</el-button>
      <el-button icon="el-icon-refresh" type="success" @click="initSalaries"></el-button>

    </div>
    <div style="margin-top: 10px">
      <el-table :data="salaries" boder stripe>
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column width="120" prop="name" label="帐套名称"></el-table-column>
        <el-table-column width="70" prop="basicSalary" label="基本工资"></el-table-column>
        <el-table-column width="70" prop="trafficSalary" label="交通补助"></el-table-column>
        <el-table-column width="70" prop="lunchSalary" label="午餐补助"></el-table-column>
        <el-table-column width="70" prop="bonus" label="奖金"></el-table-column>
        <el-table-column width="100" prop="createDate" label="启用时间"></el-table-column>
        <el-table-column label="养老金" align="center">
          <el-table-column label="比率" width="70" prop="pensionPer"></el-table-column>
          <el-table-column label="基数" width="70" prop="pensionBase"></el-table-column>
        </el-table-column>
        <el-table-column label="医疗保险" align="center">
          <el-table-column label="比率" width="70" prop="medicalPer"></el-table-column>
          <el-table-column label="基数" width="70" prop="medicalBase"></el-table-column>
        </el-table-column>
        <el-table-column label="公积金" align="center">
          <el-table-column label="比率" width="70" prop="accumulationFundPer"></el-table-column>
          <el-table-column label="基数" width="70" prop="accumulationFundBase"></el-table-column>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button size="mini" @click="showEditSalaryView(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="deleteSalary(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
        :title="dialogTitle"
        :visible.sync="dialogVisible"
        width="50%">
      <div style="display: flex;justify-content: space-around;align-items: center">
        <el-steps direction="vertical" :active="activeItemIndex">
          <el-step :title="itemName" v-for="(itemName,index) in salaryItemName" :key="index"></el-step>
        </el-steps>
        <!--value:对应对象的值，title对应对象的key-->
        <el-input v-model="salary[title]" :placeholder="'请输入'+salaryItemName[index]+'...'"
                  v-for="(value,title,index) in salary" :key="index"
                  v-show="activeItemIndex==index" style="width: 200px"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteStep">取消</el-button>
          <el-button @click="preStep">上一步</el-button>
    <el-button type="primary" @click="nextStep">{{ activeItemIndex == 10 ? "完成" : "下一步" }}</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "SalSob.vue",
  data() {
    return {
      dialogVisible: false,
      dialogTitle: '添加工资帐套',
      salaries: [],
      activeItemIndex: 0,//激活项
      salaryItemName: [
        '基本工资',
        '交通补助',
        '午餐补助',
        '奖金',
        '养老金比率',
        '养老金基数',
        '医疗保险比率',
        '医疗保险基数',
        '公积金比率',
        '公积金基数',
        '帐套名称'
      ],
      salary: {
        basicSalary: 0,
        trafficSalary: 0,
        lunchSalary: 0,
        bonus: 0,
        pensionPer: 0,
        pensionBase: 0,
        medicalPer: 0,
        medicalBase: 0,
        accumulationFundPer: 0,
        accumulationFundBase: 0,
        name: ''
      }
    }
  },
  mounted() {
    this.initSalaries();
  },
  methods: {
    /*编辑工资帐套*/
    showEditSalaryView(data) {
      this.activeItemIndex=0;
      this.dialogTitle = '修改工资帐套';
      this.dialogVisible=true;
      //salary中数据的顺序不一定跟data中一样
      //不能用以下代码进行修改
      //this.salary=data;
      this.salary.basicSalary = data.basicSalary;
      this.salary.trafficSalary = data.trafficSalary;
      this.salary.lunchSalary = data.lunchSalary;
      this.salary.bonus = data.bonus;
      this.salary.pensionPer = data.pensionPer;
      this.salary.pensionBase = data.pensionBase;
      this.salary.medicalPer = data.medicalPer;
      this.salary.medicalBase = data.medicalBase;
      this.salary.accumulationFundPer = data.accumulationFundPer;
      this.salary.accumulationFundBase = data.accumulationFundBase;
      this.salary.name = data.name;
      this.salary.id = data.id;
    },
    /*删除帐套*/
    deleteSalary(data) {
      this.$confirm('此操作将删除【' + data.name + '】帐套，是否继续？', '提示', {
        cancelButtonText: '取消',
        confirmButtonText: '确定'
      }).then(() => {
        this.deleteRequest("/salary/sob/" + data.id).then(resp => {
          if (resp) {
            this.initSalaries();
          }
        })
      }).catch(() => {
        this.$message.info("取消删除！")
      })
    },
    /*取消添加帐套*/
    deleteStep() {
      //2.关闭对话框
      this.dialogVisible = false;
      return;
    }
    ,
    /*添加账单中的上一步*/
    preStep() {
      if (this.activeItemIndex == 0) {
        return;
      }
      this.activeItemIndex--;
    }
    ,
    /*添加账单中的下一步*/
    nextStep() {
      if (this.activeItemIndex == 10) {
        if (this.salary.id){
          this.putRequest("/salary/sob/",this.salary).then(resp=>{
            if(resp) {
              this.initSalaries();
              this.dialogVisible = false;
            }
            })
        }else {
          this.postRequest("/salary/sob/", this.salary).then(resp => {
            if (resp) {
              this.initSalaries();
              this.dialogVisible = false;
            }
          });
        }
        return;
      }
      this.activeItemIndex++;
    }
    ,
    /*添加账单*/
    showAddSalaryView() {
      //数据初始化
      this.salary = {
        basicSalary: 0,
        trafficSalary: 0,
        lunchSalary: 0,
        bonus: 0,
        pensionPer: 0,
        pensionBase: 0,
        medicalPer: 0,
        medicalBase: 0,
        accumulationFundPer: 0,
        accumulationFundBase: 0,
        name: ''
      },
          this.dialogTitle = '添加工资帐套';
      this.activeItemIndex = 0;
      this.dialogVisible = true;
    }
    ,
    initSalaries() {
      this.getRequest("/salary/sob/").then(resp => {
        if (resp) {
          this.salaries = resp;
        }
      })
    }
  }
}
</script>

<style scoped>

</style>