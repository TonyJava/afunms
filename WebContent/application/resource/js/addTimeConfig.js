function addTimeConfigRow() {
	$("#addTimeConfigRow").click(function () {
		var num = $("#timeConfigTable tr").length-1;
		var t = $("<tr>").css({"border":"1"});
		t.append($("<td>").html("*开始时间").css({"border":"1 solid #000000","width":"70px"}));
		hourSelect("startHour","  时",23,1,"startHour"+num).appendTo(t).css("border","1 solid #000000");
		hourSelect("startMin","  分",55,5,"startMin"+num).appendTo(t).css("border","1 solid #000000");
		t.append($("<td>").html("*结束时间").css({"border":"1 solid #000000","width":"70px"}));
		hourSelect("endHour","  时",23,1,"endHour"+num).appendTo(t).css("border","1 solid #000000");
		hourSelect("endMin","  分",55,5,"endMin"+num).appendTo(t).css("border","1 solid #000000");
		var del = $("<span>").html("<a href='javascript:;'>删除</a>").click(function(){$(this).parent().parent().remove();});
		t.append($("<td>").append(del).css({"border":"1 solid #000000","width":"50px"}).attr("align","center"));
		t.insertBefore("#timeConfigTable tr:last");
		changeSel(num);
		addFourMin(num);
	});
}
function hourSelect(name, content, end, between, id1) {
	var sel = $("<select>", {"name":name,"id":id1}).css("width","50px");
	for (var i = 0; i < end+1; i += between) {
		$("<option>").attr("value", i + "").html(i + "").appendTo(sel);
	}
	return $("<td>").append(sel).append(content).css("width","80px");
}

function changeSel(num){
	$("#startHour"+num).change(function(){
		$("#endHour"+num).empty();
		$("#endMin"+num).empty();
		for (var i = parseInt($("#startHour"+num).val()); i < 24; i++) {
			$("<option>").attr("value", i + "").html(i + "").appendTo("#endHour"+num);
		}
		if($("#startHour"+num).val() == $("#endHour"+num).val()){
			for (var i = 5 + parseInt($("#startMin"+num).val()); i < 56; i += 5) {
				$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
			}		
		}
		addFourMin(num);
	});
	$("#startMin"+num).change(function(){
		$("#endMin"+num).empty();
		if($("#startHour"+num).val() == $("#endHour"+num).val()){
			for (var i = 5 + parseInt($("#startMin"+num).val()); i < 56; i += 5) {
				$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
			} 
		}else{ 
			for (var i = 0; i < 56; i += 5) {
				$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
			}
		}
		addFourMin(num);
	});
	
	$("#endHour"+num).change(function(){
		$("#endMin"+num).empty();
		if($("#startHour"+num).val() == $("#endHour"+num).val()){
			for (var i = 5 + parseInt($("#startMin"+num).val()); i < 56; i += 5) {
				$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
			}
		}else{
			for (var i = 0; i < 56; i += 5) {
				$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
			}
		}
		addFourMin(num);
	});
}

function addFourMin(num){
	for (var i = 56; i < 60; i++) {
		$("<option>").attr("value", i + "").html(i + "").appendTo("#endMin"+num);
	}
}


function selectedAll(s1,s2,s3,s4,num){
	$("#startHour"+num).val(s1);
	$("#startMin"+num).val(s2);
	$("#endHour"+num).empty().append($("<option>").attr("value", s3 + "").html(s3 + ""));
	$("#endMin"+num).empty().append($("<option>").attr("value", s4 + "").html(s4 + ""));
}
