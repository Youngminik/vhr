<template xmlns="http://www.w3.org/1999/html">
  <div>
    <div>
      <div style="display: flex;justify-content: space-between">
        <div>
          <el-input placeholder="请输入员工名进行搜索，可以直接回车搜索..." prefix-icon="el-icon-search"
                    clearable
                    @clear="initEmps"
                    style="width: 350px;margin-right: 10px" v-model="keyword"
                    @keydown.enter.native="initEmps"
                    :disabled="showAdvanceSearchView"></el-input>
          <el-button type="primary" icon="el-icon-search" @click="initEmps" :disabled="showAdvanceSearchView">搜索
          </el-button>
          <el-button type="primary" @click="showAdvanceSearchView=!showAdvanceSearchView">
            <i :class="showAdvanceSearchView?'fa fa-angle-double-up':'fa fa-angle-double-down'" aria-hidden="true"></i>
            高级搜索
          </el-button>
        </div>
        <div>
          <el-upload
              :before-upload="beforeUpload"
              :show-file-list="false"
              :on-success="onSuccess"
              :on-error="onError"
              :disabled="importDataDisabled"
              style="display: inline-flex;margin-right: 8px"
              action="/employee/basic/import">
            <el-button :disabled="importDataDisabled" type="success" :icon="importDataBtnIcon"> {{
                importDataBtnText
              }}
            </el-button>
          </el-upload>
          <el-button type="success" @click="exportData" icon="el-icon-download"> 导出数据
          </el-button>
          <el-button type="primary" icon="el-icon-plus" @click="showAddEmpView"> 添加员工
          </el-button>
        </div>
      </div>
      <transition name="slide-fade">
        <div v-show="showAdvanceSearchView" style="border:1px solid #409eff;border-radius: 5px;box-sizing: border-box;padding: 5px  ;
margin: 10px 0px;background-color:  #fff">
          <el-row>
            <el-col :span="5" style="font-size: 14px">
              政治面貌：
              <el-select v-model="searchValue.politicId" placeholder="政治面貌" size="mini" style="width: 120px">
                <el-option
                    v-for="item in politicsstatus"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4" style="font-size: 14px">
              民族：
              <el-select v-model="searchValue.nationId" placeholder="民族" size="mini" style="width: 100px">
                <el-option
                    v-for="item in nations"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4" style="font-size: 14px">
              职位：
              <el-select v-model="searchValue.posId" placeholder="职位" size="mini" style="width: 120px">
                <el-option
                    v-for="item in positions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4" style="font-size: 14px">
              职称：
              <el-select v-model="searchValue.jobLevelId" placeholder="职称" size="mini" style="width: 120px">
                <el-option
                    v-for="item in joblevels"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="7" style="font-size: 14px">
              聘用形式：
              <el-radio-group v-model="searchValue.engageForm" style="width: 150px" size="mini">
                <el-radio label="劳动合同">劳动合同</el-radio>
                <el-radio label="劳务合同">劳务合同</el-radio>
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row style="margin-top: 10px;font-size: 14px">
            <el-col :span="5">
              所属部门：
              <el-popover
                  placement="right"
                  title="请选择部门"
                  width="200"
                  trigger="manual"
                  v-model="popVisible">
                <el-tree default-expand-all :data="allDeps" :props="defaultProps"
                         @node-click="searchViewHandleNodeClick"></el-tree>
                <div slot="reference" style="width: 130px;display: inline-flex;font-size: 13px;border: 1px solid #dedede;height: 25px;
border-radius: 5px;cursor: pointer;align-content: center;padding-left: 8px;box-sizing: border-box;margin-left: 3px"
                     @click="showDepView">
                  {{ inputDepName }}
                </div>
              </el-popover>
            </el-col>
            <el-col :span="10" style="font-size: 14px">
              入职日期：
              <!--  unlink-panels属性解除联动。          -->
              <el-date-picker
                  style="width: 250px"
                  unlink-panels
                  size="mini"
                  v-model="searchValue.beginDateScope"
                  type="daterange"
                  value-format="yyyy-MM-dd"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期">
              </el-date-picker>
            </el-col>
            <el-col style="margin-left: 100px" :span="5" :offset="4">
              <el-button size="mini">取消</el-button>
              <el-button size="mini" icon="el-icon-search" type="primary" @click="initEmps('advanced')">搜索</el-button>
            </el-col>
          </el-row>
        </div>
      </transition>
      <!--flex：将该列固定不动    -->
      <div style="margin-top: 10px">
        <el-table
            :data="emps"
            border
            v-loading="loading"
            element-loading-text="正在加载中..."
            element-loading-spinner="el-icon-loading"
            element-loading-background="rgba(0, 0, 0, 0.8)"
            style="width: 100%">
          <el-table-column
              type="selection"
              width="55">
          </el-table-column>
          <el-table-column
              prop="name"
              label="姓名"
              fixed
              align="left"
              width="70">
          </el-table-column>
          <el-table-column
              prop="workID"
              label="工号"
              width="85"
              align="left">
          </el-table-column>
          <el-table-column
              prop="gender"
              label="性别"
              width="85"
              align="left">
          </el-table-column>
          <el-table-column
              prop="birthday"
              width="95"
              align="left"
              label="出生日期">
          </el-table-column>
          <el-table-column
              prop="idCard"
              width="150"
              align="left"
              label="身份证号码">
          </el-table-column>
          <el-table-column
              prop="wedlock"
              width="50"
              label="婚姻状况">
          </el-table-column>
          <el-table-column
              prop="nation.name"
              width="50"
              label="民族">
          </el-table-column>
          <el-table-column
              prop="nativePlace"
              width="70"
              label="籍贯">
          </el-table-column>
          <el-table-column
              prop="politicsstatus.name"
              label="政治面貌">
          </el-table-column>
          <el-table-column
              prop="email"
              width="180"
              align="left"
              label="电子邮件">
          </el-table-column>
          <el-table-column
              prop="phone"
              width="100"
              align="left"
              label="电话号码">
          </el-table-column>
          <el-table-column
              prop="address"
              width="220"
              align="left"
              label="联系地址">
          </el-table-column>
          <el-table-column
              prop="department.name"
              width="100"
              align="left"
              label="所属部门">
          </el-table-column>
          <el-table-column
              prop="jobLevel.name"
              width="100"
              align="left"
              label="职称">
          </el-table-column>
          <el-table-column
              prop="engageForm"
              width="80"
              align="left"
              label="聘用形式">
          </el-table-column>
          <el-table-column
              prop="tiptopDegree"
              width="80"
              align="left"
              label="最高学历">
          </el-table-column>
          <el-table-column
              prop="specialty"
              width="150"
              align="left"
              label="专业">
          </el-table-column>
          <el-table-column
              prop="school"
              width="150"
              align="left"
              label="毕业院校">
          </el-table-column>
          <el-table-column
              prop="beginDate"
              width="95"
              align="left"
              label="入职日期">
          </el-table-column>
          <el-table-column
              prop="conversionTime"
              width="95"
              align="left"
              label="转正日期">
          </el-table-column>
          <el-table-column
              prop="beginContract"
              width="95"
              align="left"
              label="合同起始日期">
          </el-table-column>
          <el-table-column
              prop="endContract"
              width="95"
              align="left"
              label="合同截止日期">
          </el-table-column>
          <el-table-column
              prop="contractTerm"
              width="100"
              align="left"
              label="合同期限">
            <template slot-scope="scope">
              <el-tag>{{ scope.row.contractTerm }}</el-tag>
              年
            </template>
          </el-table-column>
          <el-table-column
              label="操作"
              fixed="right"
              width="200">
            <template slot-scope="scope">
              <el-button @click="showEditEmpView(scope.row)" style="padding: 3px" size="mini">编辑</el-button>
              <el-button style="padding: 3px" size="mini" type="primary">查看高级资料</el-button>
              <el-button @click="deleteEmp(scope.row)" style="padding: 3px" size="mini" type="danger">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="display: flex;justify-content: flex-end">
        <el-pagination
            @current-change="currentChange"
            @size-change="sizeChange"
            background
            layout="sizes, prev, pager, next, jumper, ->, total, slot"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <el-dialog
        :title="title"
        :visible.sync="dialogVisible"
        width="80%">
      <div>
        <el-form :model="emp" :rules="rules" ref="empForm">
          <el-row>
            <el-col :span="6">
              <el-form-item label="姓名:" prop="name">
                <el-input size="mini" style="width: 150px" placeholder="请输入员工姓名" prefix-icon="el-icon-edit"
                          v-model="emp.name"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="性别:" prop="gender">
                <el-radio-group v-model="emp.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="出生日期:" prop="birthday">
                <el-date-picker
                    v-model="emp.birthday"
                    type="date"
                    size="mini"
                    value-format="yyyy-mm-dd"
                    style="width: 150px"
                    placeholder="出生日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="政治面貌:" prop="politicId">
                <el-select v-model="emp.politicId" placeholder="政治面貌" size="mini" style="width: 200px">
                  <el-option
                      v-for="item in politicsstatus"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="民族:" prop="nation">
                <el-select v-model="emp.nationId" placeholder="民族" size="mini" style="width: 150px">
                  <el-option
                      v-for="item in nations"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="籍贯:" prop="nativePlace">
                <el-input size="mini" style="width: 120px" placeholder="请输入籍贯" prefix-icon="el-icon-edit"
                          v-model="emp.nativePlace"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="电子邮箱:" prop="email">
                <el-input size="mini" style="width: 150px" placeholder="请输入电子邮箱" prefix-icon="el-icon-message"
                          v-model="emp.email"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="联系地址:" prop="address">
                <el-input size="mini" style="width: 200px" placeholder="请输入联系地址" prefix-icon="el-icon-edit"
                          v-model="emp.address"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="职位:" prop="posId">
                <el-select v-model="emp.posId" placeholder="职位" size="mini" style="width: 140px">
                  <el-option
                      v-for="item in positions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="职称:" prop="jobLevelId">
                <el-select v-model="emp.jobLevelId" placeholder="职称" size="mini" style="width: 140px">
                  <el-option
                      v-for="item in joblevels"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="所属部门:" prop="departmentId">
                <el-popover
                    placement="right"
                    title="请选择部门"
                    width="200"
                    trigger="manual"
                    v-model="popVisible">
                  <el-tree default-expand-all :data="allDeps" :props="defaultProps"
                           @node-click="handleNodeClick"></el-tree>
                  <div slot="reference" style="width: 150px;display: inline-flex;font-size: 13px;border: 1px solid #dedede;height: 25px;
border-radius: 5px;cursor: pointer;align-content: center;padding-left: 8px;box-sizing: border-box" @click="showDepView">
                    {{ inputDepName }}
                  </div>
                </el-popover>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="电话号码:" prop="phone">
                <el-input size="mini" style="width: 200px" placeholder="电话号码" prefix-icon="el-icon-edit"
                          v-model="emp.phone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="工号:" prop="workID">
                <el-input size="mini" style="width: 150px" placeholder="工号" prefix-icon="el-icon-edit"
                          v-model="emp.workID" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="学历:" prop="tiptopDegree">
                <el-select v-model="emp.tiptopDegree" placeholder="学历" size="mini" style="width: 150px">
                  <el-option
                      v-for="item in tiptopDegree"
                      :key="item "
                      :label="item "
                      :value="item ">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="毕业院校:" prop="school">
                <el-input size="mini" style="width: 150px" placeholder="毕业院校名称" prefix-icon="el-icon-edit"
                          v-model="emp.school"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="专业名称:" prop="specialty">
                <el-input size="mini" style="width: 200px" placeholder="请输入专业名称" prefix-icon="el-icon-edit"
                          v-model="emp.specialty"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="入职日期:" prop="beginDate">
                <el-date-picker
                    v-model="emp.beginDate"
                    type="date"
                    size="mini"
                    value-format="yyyy-mm-dd"
                    style="width: 130px"
                    placeholder="入职日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="转正日期:" prop="conversionTime">
                <el-date-picker
                    v-model="emp.conversionTime"
                    type="date"
                    size="mini"
                    value-format="yyyy-mm-dd"
                    style="width: 130px"
                    placeholder="转正日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="合同起始日期:" prop="beginContract">
                <el-date-picker
                    v-model="emp.beginContract"
                    type="date"
                    size="mini"
                    value-format="yyyy-mm-dd"
                    style="width: 130px"
                    placeholder="合同起始日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="合同终止日期:" prop="endContract">
                <el-date-picker
                    v-model="emp.endContract"
                    type="date"
                    size="mini"
                    value-format="yyyy-mm-dd"
                    style="width: 130px"
                    placeholder="合同终止日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="身份证号码:" prop="idCard">
                <el-input size="mini" style="width: 180px" placeholder="请输入身份证号码" prefix-icon="el-icon-edit"
                          v-model="emp.idCard"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="聘用形式:" prop="engageForm">
                <el-radio-group v-model="emp.engageForm">
                  <el-radio label="劳动合同">劳动合同</el-radio>
                  <el-radio label="劳务合同">劳务合同</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="婚姻状况:" prop="wedlock">
                <el-radio-group v-model="emp.wedlock">
                  <el-radio label="已婚">已婚</el-radio>
                  <el-radio label="未婚">未婚</el-radio>
                  <el-radio label="离异">离异</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="doAddEmp">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "EmpBasic.vue",
  data() {
    return {
      //高级搜索的数据
      searchValue: {
        politicId: null,
        nationId: null,
        jobLevelId: null,
        posId: null,
        engageForm: null,
        departmentId: null,
        beginDateScope: null
      },
      importDataBtnText: '导入数据',
      importDataBtnIcon: 'el-icon-upload2',
      importDataDisabled: false,//在文件进行上传时应禁止别的文件进行上传
      showAdvanceSearchView: false,//高级搜索
      emps: [],
      loading: false,
      total: 0,
      page: 1,
      keyword: '',
      size: 10,
      dialogVisible: false,
      nations: [],
      joblevels: [],
      politicsstatus: [],
      positions: [],
      popVisible: false,
      allDeps: [],
      inputDepName: '所属部门...',
      title: '',
      tiptopDegree: ['本科', '大专', '硕士', '博士', '高中', '初中', '小学', '其他'],
      emp: {
        name: "悲伤蛙",
        gender: "女",
        birthday: "1990-00-01",
        idCard: "610122199001011256",
        wedlock: "未婚",
        nationId: 1,
        jobLevelId: 9,
        nativePlace: "陕西",
        politicId: 13,
        email: "laowang@qq.com",
        phone: "18565558897",
        address: "深圳市南山区",
        departmentId: null,
        posId: 29,
        engageForm: "劳务合同",
        tiptopDegree: "本科",
        specialty: "信息管理与信息系统",
        school: "深圳大学",
        beginDate: "2017-00-31",
        workState: "在职",
        workID: "00000001",
        contractTerm: 2.0,
        conversionTime: "2018-00-31",
        notworkDate: null,
        beginContract: "2017-00-31",
        endContract: "2019-00-31",
        workAge: null,
      },

      defaultProps: {
        children: 'children',
        label: 'name'
      },
      rules: {
        name: [{required: true, message: "用户名不能为空", trigger: 'blur'}],
        gender: [{required: true, message: "性别不能为空", trigger: 'blur'}],
        birthday: [{required: true, message: "出生日期不能为空", trigger: 'blur'}],
        idCard: [{required: true, message: "身份证不能为空", trigger: 'blur'}, {
          pattern: /(^\d{15}$)|(^\d{17}([0-9]|X)$)/,
          message: '身份证号码格式不正确',
          trigger: 'blur'
        }],
        wedlock: [{required: true, message: "婚姻状况不能为空", trigger: 'blur'}],
        nationId: [{required: true, message: "民族不能为空", trigger: 'blur'}],
        jobLevelId: [{required: true, message: "职称不能为空", trigger: 'blur'}],
        nativePlace: [{required: true, message: "籍贯不能为空", trigger: 'blur'}],
        politicId: [{required: true, message: "政治面貌不能为空", trigger: 'blur'}],
        email: [{required: true, message: "邮箱地址不能为空", trigger: 'blur'}, {
          type: 'email', message: '邮箱格式不正确',
          trigger: 'blur'
        }],
        phone: [{required: true, message: "手机号码不能为空", trigger: 'blur'}],
        address: [{required: true, message: "地址不能为空", trigger: 'blur'}],
        departmentId: [{required: true, message: "部门名称不能为空", trigger: 'blur'}],
        posId: [{required: true, message: "职位不能为空", trigger: 'blur'}],
        engageForm: [{required: true, message: "聘用形式不能为空", trigger: 'blur'}],
        specialty: [{required: true, message: "专业不能为空", trigger: 'blur'}],
        school: [{required: true, message: "毕业院校不能为空", trigger: 'blur'}],
        beginDate: [{required: true, message: "入职日期不能为空", trigger: 'blur'}],
        workState: [{required: true, message: "工作状态不能为空", trigger: 'blur'}],
        contractTerm: [{required: true, message: "合同期限不能为空", trigger: 'blur'}],
        conversionTime: [{required: true, message: "转正日期不能为空", trigger: 'blur'}],
        notworkDate: [{required: true, message: "离职日期不能为空", trigger: 'blur'}],
        beginContract: [{required: true, message: "合同起始日期不能为空", trigger: 'blur'}],
        endContract: [{required: true, message: "合同结束日期不能为空", trigger: 'blur'}],
        workAge: [{required: true, message: "工龄不能为空", trigger: 'blur'}],

      },
      options: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      value: ''
    }
  },
  mounted() {
    this.initEmps();
    this.initData();
    this.initPositions();
  }
  ,
  methods: {
    /*高级搜索*/
    searchViewHandleNodeClick(data) {
      this.inputDepName = data.name;
      this.searchValue.departmentId = data.id;
      this.popVisible = !this.popVisible;
    },
    /*上传失败的提示信息*/
    onError(err, file, fileList) {
      this.importDataBtnText = '导入数据';
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataDisabled = false;
    },
    /*上传成功之后的提示信息*/
    onSuccess(response, file, fileList) {
      this.importDataBtnText = '导入数据';
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataDisabled = false;
      this.initEmps();
    },
    /*上传之前的加载页面*/
    beforeUpload() {
      this.importDataBtnText = '正在导入';
      this.importDataBtnIcon = 'el-icon-loading';
      this.importDataDisabled = true;
    },
    //导出数据
    exportData() {
      window.open('/employee/basic/export', '_parent');
    },
    initEmps(type) {
      this.loading = true;
      let url = '/employee/basic/?page=' + this.page + '&size=' + this.size;
      if (type && type == 'advanced') {
        if (this.searchValue.politicId) {
          url += '&politicId=' + this.searchValue.politicId;
        }
        if (this.searchValue.nationId) {
          url += '&nationId=' + this.searchValue.nationId;
        }
        if (this.searchValue.jobLevelId){
          url+='&jobLevelId=' + this.searchValue.jobLevelId;
        }
        if(this.searchValue.posId){
          url+='&posId=' + this.searchValue.posId;
        }
        if(this.searchValue.engageForm){
          url+='&engageForm=' + this.searchValue.engageForm;
        }
        if(this.searchValue.departmentId){
          url+='&departmentId=' + this.searchValue.departmentId;
        }
        if(this.searchValue.beginDateScope){
          url+='&beginDateScope=' + this.searchValue.beginDateScope;
        }
      } else {
        url += "&name=" + this.keyword;
      }
      this.getRequest(url).then(resp => {
        this.loading = false;
        if (resp) {
          this.emps = resp.data;
          this.total = resp.total;
        }
      })
    }
    ,
    currentChange(currentPage) {
      this.page = currentPage;
      this.initEmps();

    }
    ,
    sizeChange(currentSize) {
      this.size = currentSize;
      this.initEmps();
    },
    /*面板添加用户*/
    showAddEmpView() {
      this.emptyEmp();
      this.title = '添加员工';
      this.getMaxWorkID();
      this.dialogVisible = true;
    },
    /*从后端传nation、joblevel、politicsstatus数据*/
    initData() {
      if (!window.sessionStorage.getItem("nations")) {
        this.getRequest("/employee/basic/nations").then(resp => {
          if (resp) {
            this.nations = resp;
            window.sessionStorage.setItem("nations", JSON.stringify(resp));
          }
        })
      } else {
        this.nations = JSON.parse(window.sessionStorage.getItem("nations"));
      }
      if (!window.sessionStorage.getItem("joblevels")) {
        this.getRequest("/employee/basic/joblevels").then(resp => {
          if (resp) {
            this.joblevels = resp;
            window.sessionStorage.setItem("joblevels", JSON.stringify(resp));
          }
        })
      } else {
        this.joblevels = JSON.parse(window.sessionStorage.getItem("joblevels"));
      }
      if (!window.sessionStorage.getItem("politicsstatus")) {
        this.getRequest("/employee/basic/politicsstatus").then(resp => {
          if (resp) {
            this.politicsstatus = resp;
            window.sessionStorage.setItem("politicsstatus", JSON.stringify(resp));
          }
        })
      } else {
        this.politicsstatus = JSON.parse(window.sessionStorage.getItem("politicsstatus"));
      }
      if (!window.sessionStorage.getItem("deps")) {
        this.getRequest("/employee/basic/deps").then(resp => {
          if (resp) {
            this.allDeps = resp;
            window.sessionStorage.setItem("deps", JSON.stringify(resp));
          }
        })
      } else {
        this.allDeps = JSON.parse(window.sessionStorage.getItem("deps"));
      }
    },
    initPositions() {
      this.getRequest("/employee/basic/positions").then(resp => {
        if (resp) {
          this.positions = resp;
        }
      })
    },
    getMaxWorkID() {
      this.getRequest("/employee/basic/maxWorkID").then(resp => {
        if (resp) {
          this.emp.workID = resp.obj;
        }
      })
    },
    showDepView() {
      this.popVisible = !this.popVisible;
    },
    handleNodeClick(data) {
      this.inputDepName = data.name;
      this.emp.departmentId = data.id;
      /*this.popVisible = !this.popVisible;*/
    },
    /*添加用户*/
    doAddEmp() {
      if (this.emp.id) {
        this.$refs['empForm'].validate(valid => {
          if (valid) {
            this.putRequest("/employee/basic/", this.emp).then(resp => {
              if (resp) {
                this.dialogVisible = false;
                this.initEmps();
              }
            })
          }
        })
      } else {
        this.$refs['empForm'].validate(valid => {
          if (valid) {
            this.postRequest("/employee/basic/", this.emp).then(resp => {
              if (resp) {
                this.dialogVisible = false;
                this.initEmps();
              }
            })
          }
        })
      }
    },
    /*删除部门*/
    deleteEmp(data) {
      this.$confirm('此操作将永久删除【' + data.name + '】, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRequest("/employee/basic/" + data.id).then(resp => {
          if (resp) {
            this.initEmps();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    /*编辑*/
    showEditEmpView(data) {
      this.title = '编辑员工信息';
      this.emp = data;
      this.inputDepName = data.department.name;
      this.dialogVisible = true;
    },
    /*添加完成后，emp置空*/
    emptyEmp() {
      this.emp = {
        name: "",
        gender: "",
        birthday: "",
        idCard: "",
        wedlock: "",
        nationId: '',
        jobLevelId: '',
        nativePlace: "",
        politicId: '',
        email: "",
        phone: "",
        address: "",
        departmentId: null,
        posId: '',
        engageForm: "",
        tiptopDegree: "",
        specialty: "",
        school: "",
        beginDate: "",
        workID: "",
        contractTerm: 2.0,
        conversionTime: "",
        notworkDate: null,
        beginContract: "",
        endContract: "",
        workAge: null,
      },

          this.inputDepName = '';
    }
  }
}
</script>

<style>
/* 可以设置不同的进入和离开动画 */
/* 设置持续时间和动画函数 */
.slide-fade-enter-active {
  transition: all .8s ease;
}

.slide-fade-leave-active {
  transition: all .3s cubic-bezier(1.0, 0.5, 0.8, 1.0);
}

.slide-fade-enter, .slide-fade-leave-to
  /* .slide-fade-leave-active for below version 2.1.8 */
{
  transform: translateX(10px);
  opacity: 0;
}
</style>