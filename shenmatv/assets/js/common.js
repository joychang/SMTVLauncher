
	
function objectToJson(obj){
	 var A = obj;
  var isArray = function(v){
      return v && typeof v.length == 'number' && typeof v.splice == 'function';
  }
  var isDate = function(v){
       return v && typeof v.getFullYear == 'function';
  }
  var pad = function(n) {
      return n < 10 ? "0" + n : n
  };
  var W = "";
  if (typeof A == "object") {
      if (isArray(A)) {
          for (var i = 0; i < A.length; i++) {
              if (typeof A[i] == "object")
                  W += (W == "" ? "" : ",") + objectToJson(A[i]);
              else if (typeof A[i] == "string")
                  W += (W == "" ? "" : ",") + "\"" + A[i].replace("\"", "\\\"") + "\"";
              else if (typeof A[i] == "number" || typeof A[i] == "boolean")
                  W += (W == "" ? "" : ",") + A[i] + "";
          }
          W = "[" + W + "]";
      } else if (isDate(A)) {
          W += "\"" + A.getFullYear() + "-" + pad(A.getMonth() + 1) + "-" + pad(A.getDate()) + "T" + pad(A.getHours()) + ":" + pad(A.getMinutes()) + ":" + pad(A.getSeconds()) + "\""
      } else {
          for (var p in A) {
              if (typeof A[p] == "object")
                  W += (W == "" ? "" : ",") +"\""+ p + "\":" + objectToJson(A[p]);
              else if (typeof A[p] == "string")
                  W += (W == "" ? "" : ",") +"\""+ p + "\":\"" + A[p].replace("\"", "\\\"") + "\"";
              else if (typeof A[p] == "number" || typeof A[p] == "boolean")
                  W += (W == "" ? "" : ",") + "\""+ p + "\":" + A[p] + "";
          }
          W = "{" + W + "}";
      }
  }
  return W;
}


function byte_format($size, $dec){
	   $a = new Array("B", "KB", "MB", "GB", "TB", "PB");
	   $pos = 0;
	    while($size >= 1024) {
	         $size /= 1024;
	         $pos++;
	   }
  return Math.round($size,$dec)+" "+$a[$pos];
}
