<template>
  <div>
    <el-form
        :rules="rules"
        ref="loginForm"
        :model="loginForm"
        class="loginContainter"
        v-loading="loading"
        element-loading-text="正在登录..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.8)">
      <h3 class="loginTitle">系统登录</h3>
      <el-form-item prop="username">
        <el-input size="normal" type="text" v-model="loginForm.username" auto-complete="off"
                  placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input size="normal" type="text" v-model="loginForm.password" auto-complete="off" placeholder="请输入密码"
                  @keydown.e.enter.native="submitLogin"></el-input>
      </el-form-item>

      <el-checkbox size="normal" class="loginRemember" v-model="checked">记住我</el-checkbox>
      <el-button size="normal" type="primary" style="width: 100%;" @click="submitLogin">登录</el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: '123'
      },
      checked: true,
      rules: {
        username: [{required: true, message: "用户名不能为空", trigger: 'blur'}],
        password: [{required: true, message: "密码不能为空", trigger: 'blur'}]
      },
      loading: false
    }
  },
  methods: {
    submitLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          // alert('submit!');
          this.postKeyValueRequest('/doLogin', this.loginForm).then(resp => {
            if (resp) {
              //判断登录时聊天系统需要的currentHr是否为null
              this.$store.commit('INIT_CURRENTHR',resp.obj);
              this.loading=false;
              //保存当前用户到vuex
              this.$store.state.currentUser=resp.obj;
              //保存登录用户到sessionStorage中
              /*在实际开放发中，登录之后不能弹出用户的个人信息，对以下代码进行修改*/
              //alert(JSON.stringify(resp))
              window.sessionStorage.setItem("user", JSON.stringify(resp.obj));
              let path = this.$route.query.redirect;
              this.$router.replace((path == '/' || path == undefined) ? '/home' : path);
            }
          })
        } else {
          this.$message.error('用户名或密码错误！');
          return false;
        }
      });
    }
  }

}

</script>

<style>
.loginContainter {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 180px auto;
  width: 350px;
  padding: 15px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.loginTitle {
  margin: 15px auto 20px auto;
  text-align: center;
  color: #505458;
}

.loginRemember {
  text-align: left;
  margin: 0px 0px 15px 0px;
}
</style>