<%--
  Created by IntelliJ IDEA.
  User: 李浩然
  Date: 2017/5/26
  Time: 2:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title><spring:message code="title.add_invoice" /></title>

    <!-- Bootstrap -->
    <link href="../vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="../vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="../vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- iCheck -->
    <link href="../vendors/iCheck/skins/flat/green.css" rel="stylesheet">
    <!-- bootstrap-wysiwyg -->
    <link href="../vendors/google-code-prettify/bin/prettify.min.css" rel="stylesheet">
    <!-- Select2 -->
    <link href="../vendors/select2/dist/css/select2.min.css" rel="stylesheet">
    <!-- Switchery -->
    <link href="../vendors/switchery/dist/switchery.min.css" rel="stylesheet">
    <!-- starrr -->
    <link href="../vendors/starrr/dist/starrr.css" rel="stylesheet">
    <!-- bootstrap-daterangepicker -->
    <link href="../vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="../build/css/custom.min.css" rel="stylesheet">

    <style>
        .file {
            position: relative;
            display: inline-block;
            background: #D0EEFF;
            border: 1px solid #99D3F5;
            border-radius: 4px;
            padding: 4px 12px;
            overflow: hidden;
            color: #1E88C7;
            text-decoration: none;
            text-indent: 0;
            line-height: 20px;
        }
        .file input {
            position: absolute;
            font-size: 100px;
            right: 0;
            top: 0;
            opacity: 0;
        }
        .file:hover {
            background: #AADFFD;
            border-color: #78C3F3;
            color: #004974;
            text-decoration: none;
        }
    </style>

    <script type="text/javascript">
        //定义id选择器
        function Id(id){
            return document.getElementById(id);
        }
        //入口函数，两个参数分别为<input type='file'/>的id，还有一个就是图片的id，然后会自动根据文件id得到图片，然后把图片放到指定id的图片标签中
        function changeToop(fileid,imgid){
            var file = Id(fileid);
            if(file.value==''){
                //设置默认图片
                Id("myimg").src='';
            }else{
                preImg(fileid,imgid);
            }
        }
        //获取input[file]图片的url Important
        function getFileUrl(fileId) {
            var url;
            var file = Id(fileId);
            var agent = navigator.userAgent;
            if (agent.indexOf("MSIE")>=1) {
                url = file.value;
            } else if(agent.indexOf("Firefox")>0) {
                url = window.URL.createObjectURL(file.files.item(0));
            } else if(agent.indexOf("Chrome")>0) {
                url = window.URL.createObjectURL(file.files.item(0));
            }
            var extIndex = file.value.lastIndexOf(".");
            var ext = file.value.substring(extIndex,file.value.length).toUpperCase();
            if (ext != ".JPG") {
                alert("只允许上传JPG格式的图片！");
                Id('upload_file').disabled = true;
                return "";
            }
            Id('upload_file').disabled = false;
            return url;
        }
        //读取图片后预览
        function preImg(fileId,imgId) {
            var imgPre =Id(imgId);
            imgPre.src = getFileUrl(fileId);
        }
    </script>
</head>

<body class="nav-md">
<div class="container body">
    <div class="main_container">
        <%@ include file="left_top.jspf"%>

        <!-- top navigation -->
        <%@ include file="right_top.jspf"%>
        <!-- /top navigation -->

        <!-- page content -->
        <div class="right_col" role="main">
            <div class="">
                <div class="page-title">
                    <div class="title_left">
                        <h3><spring:message code="title.add_invoice" /></h3>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${has_authority}">
                        <div class="clearfix"></div>
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="x_panel">
                                    <div class="x_title">
                                        <h2><spring:message code="form.title.invoice" /></h2>
                                        <ul class="nav navbar-right panel_toolbox">
                                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
                                        </ul>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="x_content">
                                        <form action="add_invoice_image" method="post"
                                              enctype="multipart/form-data" class="form-horizontal">
                                            <div class="form-group">
                                                <label class="control-label col-md-2" for="detail_num">
                                                    待添加的发票的明细数目
                                                    <span class="required">*</span>
                                                </label>
                                                <div class="col-md-6">
                                                    <input class="form-control has-feedback-left"
                                                           id="detail_num" name="detail_num" placeholder="发票的明细数目（请输入大于0的整数）" required="required"/>
                                                    <span class="fa fa-user form-control-feedback left" aria-hidden="true"></span>
                                                </div>
                                            </div>
                                            <div class="ln_solid"></div>
                                            <div class="form-group">
                                                <div class="col-md-3 col-md-offset-2">
                                                    <a class="file"><spring:message code="tip.upload"/>
                                                        <input type="file" name="invoice_image" id="file_selector" class="btn btn-round"
                                                               placeholder="<spring:message code="tip.upload"/>"
                                                               accept=".jpg" onchange="changeToop('file_selector', 'imagePreview');">
                                                    </a>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="submit" value="<spring:message code="button.submit" />"
                                                           class="btn btn-round btn-success" id="upload_file" onclick="return checkInput(this.form);">
                                                    <script>
                                                        function checkInput(form) {
                                                            var value = form.detail_num.value;
                                                            if (value.indexOf('.') >= 0 || isNaN(Number(value)) || Number(value) == 0) {
                                                                alert("明细数目：请输入大于0的整数");
                                                                return false;
                                                            }
                                                            return true;
                                                        }
                                                    </script>
                                                </div>
                                            </div>
                                            <div class="ln_solid"></div>
                                            <div id="preview" class="col-md-8 col-md-offset-2 col-sm-8 col-sm-offset-2 col-xs-8 col-xs-offset-2">
                                                <img id="imagePreview" style="width: 1000px" src=''/>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <c:if test="${has_file}">
                            <div class="clearfix"></div>
                            <div class="row">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="x_panel">
                                        <div class="x_title">
                                            <h2><spring:message code="form.title.invoice" /></h2>
                                            <ul class="nav navbar-right panel_toolbox">
                                                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></li>
                                            </ul>
                                            <div class="clearfix"></div>
                                        </div>
                                        <div class="x_content">
                                            <form:form commandName="invoice" action="save_invoice" method="post"
                                                       cssClass="form-horizontal form-label-left"  onsubmit="return checkSubmit(${detail_num});">
                                                <input type="hidden" name="save_action_source" value="invoice_input_image">
                                                <%@ include file="invoice_edit_form.jspf"%>
                                            </form:form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <%@ include file="no_authority.jspf"%>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <!-- /page content -->

        <!-- footer content -->
        <footer>
            <div class="pull-right">
                <spring:message code="text.footer"/>
            </div>
            <div class="clearfix"></div>
        </footer>
        <!-- /footer content -->
    </div>
</div>

<!-- jQuery -->
<script src="../vendors/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap -->
<script src="../vendors/bootstrap/dist/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="../vendors/fastclick/lib/fastclick.js"></script>
<!-- NProgress -->
<script src="../vendors/nprogress/nprogress.js"></script>
<!-- bootstrap-progressbar -->
<script src="../vendors/bootstrap-progressbar/bootstrap-progressbar.min.js"></script>
<!-- iCheck -->
<script src="../vendors/iCheck/icheck.min.js"></script>
<!-- bootstrap-daterangepicker -->
<script src="../vendors/moment/min/moment.min.js"></script>
<script src="../vendors/bootstrap-daterangepicker/daterangepicker.js"></script>
<!-- bootstrap-wysiwyg -->
<script src="../vendors/bootstrap-wysiwyg/js/bootstrap-wysiwyg.min.js"></script>
<script src="../vendors/jquery.hotkeys/jquery.hotkeys.js"></script>
<script src="../vendors/google-code-prettify/src/prettify.js"></script>
<!-- jQuery Tags Input -->
<script src="../vendors/jquery.tagsinput/src/jquery.tagsinput.js"></script>
<!-- Switchery -->
<script src="../vendors/switchery/dist/switchery.min.js"></script>
<!-- Select2 -->
<script src="../vendors/select2/dist/js/select2.full.min.js"></script>
<!-- Parsley -->
<script src="../vendors/parsleyjs/dist/parsley.min.js"></script>
<!-- Autosize -->
<script src="../vendors/autosize/dist/autosize.min.js"></script>
<!-- jQuery autocomplete -->
<script src="../vendors/devbridge-autocomplete/dist/jquery.autocomplete.min.js"></script>
<!-- starrr -->
<script src="../vendors/starrr/dist/starrr.js"></script>
<!-- Custom Theme Scripts -->
<script src="../build/js/custom.min.js"></script>

</body>
</html>


