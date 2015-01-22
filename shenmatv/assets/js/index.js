var uploadUrl="upload.action";//上传URL

$(function() {
	$('#fileselect').uploadify({
		'formData'     : {
			'timestamp' : '1',
			'token'     : '1'
		},
		'swf'      : './js/uploadify.swf',
		'uploader' : uploadUrl, //上传路径
		'auto':true,//自动上传
		'checkExisting':false, //action
		'debug':false,
		'buttonText':'',
		'height':'52',
		'width':'281',
		'removeTimeout':3000,
		'fileTypeExts':'*.*'
	});
});

function up(){
	$('#fileselect').uploadify('upload', '*.apk');
}


function loadFun(){
	if (window.File && window.FileList && window.FileReader) {
		Init();
	};
}

var xhr=null;
var upfiles = new Array();
function $$(id) {
	return document.getElementById(id);
}
//file drag hover
function FileDragHover(e) {
	e.stopPropagation();
	e.preventDefault();
	e.target.className = (e.type == "dragover" ? "hover" : "");
}
// file selection
function FileSelectHandler(e) {
	// cancel event and hover styling
	FileDragHover(e);
	// fetch FileList object
	var files = e.target.files || e.dataTransfer.files;
	// process all File objects
	for ( var i = 0, f; f = files[i]; i++) {
		ParseFile(f);
		upfiles.push(f);
		var reader = new FileReader();  
		reader.readAsDataURL(f);
		var con=[];
		con.push('<div id="pro_11" class="uploadify-queue-item">');
		con.push('<div class="fileName" style="line-height:20px;width:100%;font-size:13px;color:#000000;height:90px;">正在上传：'+f.name+'</div>');
		con.push('<div class="uploadify-progress">');
		con.push('<div class="uploadify-progress-bar"></div>');
		con.push('<div class="fileName" style="line-height:30px;width:100%;font-size:13px;color:#000000">'+byte_format(f.size,3)+'</div>');
		con.push('</div>');
		$('#progressBg').html(con.join(""));
		//$("#bg1").css("visibility","hidden");
		$("#okImg").hide();
		$("#bg2").show();
		
		var fileSize=f.size;
		var fileName=f.name;
		xhr.upload.addEventListener("progress", function(e) { 
		    if (e.lengthComputable) {  
		        var percentage = Math.round((e.loaded * 100) / e.total);  
		      //  img.style.opacity = 1-percentage/100.0; 
		        $('#pro_11').find('.uploadify-progress-bar').css('width',percentage+"%");
		        $('#pro_11').find('.data').html(' - '+percentage+"%");
		        if(percentage==100){
		        	$('#pro_11').find('.data').html(' - 上传完成');
		        	setTimeout(function(){
						//$("#bg2").hide();
						//$("#bg3").show();
		        		$("#okImg").show();
					},1000);
			    }
		    } else{
		    	$('#pro_11').find('.data').html(' - 上传完成');
		    	setTimeout(function(){
				//	$("#bg2").hide();
				//	$("#bg3").show();
		    		$("#okImg").show();
				},1000);
			}
		}, true);  
		 
		xhr.upload.addEventListener("load", function(e){
		}, true);  
		xhr.upload.addEventListener("error",function(e){
			alert("error");
			$('#pro_11').find('.data').html(' - 上传失败');
			setTimeout(function(){
				$("#bg2").hide();
				$("#bg4").show();
			},1000);
		},false);

		xhr.open('post', uploadUrl, true);  
		// Set appropriate headers   
		xhr.setRequestHeader("Cache-Control", "no-cache");   
		xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");   
		xhr.setRequestHeader("fileName", f.name);   
		xhr.setRequestHeader("fileSize", f.size);   
		xhr.setRequestHeader("fileType", f.type); 
	//	xhr.setRequestHeader('gridID',gridIDFlag);   
		var formData = new FormData();   
		formData.append('myFile',f);   
		xhr.send(formData);  
	}
}

// output information
function Output(msg) {
	var m = $$("messages");
//	m.innerHTML = msg + m.innerHTML;
}

// output file information
function ParseFile(file) {
	Output("<p>文件信息: <strong>" + file.name
			+ "</strong> 类型: <strong>" + file.type
			+ "</strong> 大小: <strong>" + file.size
			+ "</strong> bytes</p>");
}

//initialize
function Init() {
	var fileselect = $$("fileselect"), 
	filedrag = $$("filedrag"), submitbutton = $$("submitbutton");
	$("#filedrag").addClass("fileB");
	// file select
	fileselect.addEventListener("change", FileSelectHandler, false);

	// is XHR2 available?
	xhr = new XMLHttpRequest();
	if (xhr.upload) {
		// file drop
		filedrag.addEventListener("dragover", FileDragHover, false);
		filedrag.addEventListener("dragleave", FileDragHover, false);
		filedrag.addEventListener("drop", FileSelectHandler, false);
		filedrag.style.display = "block";
		// remove submit button
		//submitbutton.style.display = "none";
	}
}


function okAction(){
	window.location.href=window.location.href;
}

