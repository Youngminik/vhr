<template>

  <div id="uesrtext">
    <el-popover placement="top-start" width="400" trigger="click" class="emoBox">
      <div class="emotionList">
        <a href="javascript:void(0);" @click="getEmo(index)" v-for="(item,index) in faceList" :key="index" class="emotionItem">{{item}}</a>
      </div>
      <el-button id="emojiBtn" class="emotionSelect" slot="reference" size="normal">
        <icon-button icon="emoji" aria-hidden="true"></icon-button>
      </el-button>
    </el-popover>
<!--    <upload
        title="图片"
        v-model="fileUrl"
        @click.native="fileType = msgType.IMG"
    />
    <upload
        title="视频"
        icon="el-icon-video-camera"
        v-model="fileUrl"
        uploadType="video"
        @click.native="fileType = msgType.VIDEO"
    />-->
    <el-upload
        style="margin-right: 20px;height: 25px;padding-left: 8px"
        class="upload-btn"
        action="/file"
        :before-upload="beforeAvatarUpload"
        :on-success="imgSuccess"
        :on-error="imgError"
        :show-file-list="false"
        accept=".jpg,.jpeg,.png,.JPG,JPEG,.PNG,.gif,.GIF"
    >
      <icon-button  icon="image"></icon-button>
<!--      <el-button  id="uploadImgBtn" icon="el-icon-picture-outline"></el-button>-->
    </el-upload>
    <el-button id="sendBtn" type="primary"  size="mini" @click="addMessageByClick" >发送(S)</el-button>
        <textarea id="textarea" placeholder="按 Ctrl + Enter 发送" v-model="content" v-on:keyup="addMessage">
    </textarea>
<!--    <el-button id="sendBtn" type="primary" size="mini" @click="addMessageByClick" >发送(S)</el-button>-->
  </div>

</template>

<script>
import {mapState} from 'vuex'
import iconButton from "./iconButton";

const appData=require("../../utils/emoji.json")//引入存放emoji表情的json文件

export default {
  name: 'uesrtext',
  components:{
    iconButton },
  data() {
    return {
      content: '',
     /* showFace: false*/
      faceList:[],//表情包数据
      /*fileUrl: null,
      msgType: MSG_TYPE,
      fileType: null,*/
    }
  },

  computed: mapState([
    'sessions',
    'currentSession'
  ]),

  mounted(){
    for (let i in appData){//读取json文件保存数据给数组
      this.faceList.push(appData[i].char);
    }
  },
  /*methods: {

    /!*图标点击*!/
    iconClick(e) {
      let self = this;
      if (this.disabled) return;
      if (e === "emoji") {
        this.emojivisible = !this.emojivisible;
        return
      }
      this.$emit("iconClick", e)
    },

    insertFace: function (item) {
      this.content = this.content + "face" + item;
      this.showFace = false;
    },
    getEmo(index) {
      var textArea = document.getElementById('textarea');

      //将选中的表情插入到输入文本的光标之后
      function changeSelectedText(obj, str) {
        if (window.getSelection) {
          // 非IE浏览器
          textArea.setRangeText(str);
          // 在未选中文本的情况下，重新设置光标位置
          textArea.selectionStart += str.length;
          textArea.focus()
        } else if (document.selection) {
          // IE浏览器
          obj.focus();
          var sel = document.selection.createRange();
          sel.text = str;
        }
      }

      changeSelectedText(textArea, this.faceList[index]);
      this.content = textArea.value;// 要同步data中的数据
      return;
    },


    //消息的发送
    addMessage(e) {
      if (e.ctrlKey && e.keyCode === 13 && this.content.length) {
        let msgObj = new Object();
        msgObj.to = this.currentSession.username;
        msgObj.content = this.content;
        this.$store.state.stomp.send('/ws/chat', {}, JSON.stringify(msgObj));
        this.$store.commit('addMessage', msgObj);
        this.content = '';
      }
    }
  }*/
  methods: {
    addMessageByClick(){
      let msgObj=new Object();
      msgObj.content=this.content;
     /* msgObj.messageTypeId=1;
      //发送群聊消息
      if (this.currentSession.username=="群聊"){
        console.log(this.content);
        this.$store.state.stomp.send("/ws/groupChat",{},JSON.stringify(msgObj));
      }
      //给机器人发送消息
      if (this.currentSession.username=="机器人"){
        msgObj.fromNickname=this.$store.state.currentUser.nickname;
        msgObj.to='机器人';
        this.$store.state.stomp.send("/ws/robotChat",{},JSON.stringify(msgObj));
        //保存该条记录到session
        this.$store.commit('addMessage',msgObj);
      }
      //发送私聊消息
      else{*/
        // msgObj.from=this.$store.state.currentUser.username;
        // msgObj.fromNickname=this.$store.state.currentUser.nickname;
        msgObj.to=this.currentSession.username;
        this.$store.state.stomp.send("/ws/chat",{},JSON.stringify(msgObj));
        //提交私聊消息记录
        this.$store.commit('addMessage',msgObj);

      //清空输入框
      this.content='';
    },
    addMessage (e) {
      if (e.ctrlKey && e.keyCode ===13 && this.content.length) {
        this.addMessageByClick();
      }
    },
    /**
     *       图片上传的方法
     */
    //上传前
    beforeAvatarUpload(file) {
      /*//不给机器人发送图片
      if (this.currentSession.username=="机器人") {
        this.$message.error("瓦力拒绝接收你的图片！")
      }*/
      //判断图片大小
      let isLt1M = file.size / 1024 / 1024 < 1;
      console.log(file)
      if (!isLt1M) {
        this.$message.error('上传图片大小不能超过 1MB!');
      }
      //判断图片的格式
      let imgType=file.name.substring(file.name.lastIndexOf(".")+1);
      imgType=imgType.toLowerCase();
      let isImg=imgType==='jpg'|| imgType==='png'|| imgType==='jpeg'||imgType==='gif';
      if (!isImg){
        this.$message.error('上传图片格式不符合要求！');
      }
      return isLt1M&&isImg;
    },
    // 图片上传成功
    imgSuccess(response, file, fileList) {
      console.log("图片url为："+response);
      let msgObj=new Object();
      msgObj.content=response;
      //设置消息类型为图片
      msgObj.messageTypeId=2;
     /* if (this.currentSession.username=="群聊"){
        this.$store.state.stomp.send("/ws/groupChat",{},JSON.stringify(msgObj));
      }else {*/
        msgObj.from=this.$store.state.currentUser.username;
        msgObj.fromNickname=this.$store.state.currentUser.nickname;
        msgObj.to=this.currentSession.username;
        this.$store.state.stomp.send("/ws/chat",{},JSON.stringify(msgObj));
        //提交私聊消息记录
        this.$store.commit('addMessage',msgObj);

    },
    // 图片上传失败
    imgError(err, file, fileList){
      this.$message.error("上传失败");
    },
    //获取Emoji
    getEmo(index){
      var textArea=document.getElementById('textarea');
      //将选中的表情插入到输入文本的光标之后
      function changeSelectedText(obj, str) {
        if (window.getSelection) {
          // 非IE浏览器
          textArea.setRangeText(str);
          // 在未选中文本的情况下，重新设置光标位置
          textArea.selectionStart += str.length;
          textArea.focus()
        } else if (document.selection) {
          // IE浏览器
          obj.focus();
          var sel = document.selection.createRange();
          sel.text = str;
        }
      }
      changeSelectedText(textArea,this.faceList[index]);
      this.content=textArea.value;// 要同步data中的数据
      // console.log(this.faceList[index]);
      return;

    }
  }
}
</script>

<style lang="scss">
/* el-popover是和app同级的，所以scoped的局部属性设置无效 */
/* 需要设置全局style */
.el-popover{
  height:200px;
  width:450px;
  overflow: scroll;
  overflow-x:auto;
}
</style>

<style lang="scss" scoped>
#uesrtext {
/*  position:relative ;*/
  position: absolute;
  bottom: 0;
  right: 0;
  width: 100%;
  height: 30%;
  border: solid 1px #DDD;
  background-color: white;
  > textarea {
    padding: 10px;
    width: 100%;
 height:  80%;
    border: none;
    outline: none;
    resize: none;//禁止拉伸
    outline: unset;

  }
  #sendBtn{
    float: right;
    margin-right: 10px;
    margin-top: 6px;



  }
  #uploadImgBtn{
    border: none;
    padding-top: 0px;
    padding-bottom: 0px;
    margin-bottom: 0px;
    padding-left: 12px;
  }
  #uploadImgBtn:hover{
    background-color: white;
  }
  #emojiBtn{
    border: none;
    padding-right: 0px;
    padding-bottom: 0px;
    margin-bottom: 0px;
  }
  #emojiBtn:hover{
    background-color: white;
  }
  .upload-btn{
    display: inline-block;
  }
}
.emotionList{
  display: flex;
  flex-wrap: wrap;
  padding:5px;
}
.emotionItem{
  width:10%;
  font-size:20px;
  text-align:center;
}
/*包含以下四种的链接*/
.emotionItem {
  text-decoration: none;
}
/*正常的未被访问过的链接*/
.emotionItem:link {
  text-decoration: none;
}
/*已经访问过的链接*/
.emotionItem:visited {
  text-decoration: none;
}
/*鼠标划过(停留)的链接*/
.emotionItem:hover {
  text-decoration: none;
}
/* 正在点击的链接*/
.emotionItem:active {
  text-decoration: none;
}
</style>
