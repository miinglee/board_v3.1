var replyMaker = (function(){
	
	var getAll  = function(obj, callback){
		console.log("get All....");
		
		$.getJSON('/replies/'+obj,callback );
		
	};
	
	var getPagination = function(obj){
		
		var url = '/replies/paging/'+obj.pno+ '/'+obj.pageNum;
		
		$.ajax({
			type:'get',
			url: url,
			data:JSON.stringify(obj), 
			dataType:'json',
			contentType: "application/json",
			success:function(result){
				alert(result);
			}
		});
		
	}
	
	
	var getMaxSeq = function(obj, callback){
		console.log("[js] get Max Seq...");
		
		$.getJSON('/replies/getMaxSeq/'+obj,callback );
	}
	
	var add = function(obj, mode, callback){
		
		console.log("add....");
		var url;
		
		url = mode == 'ADD' ? '/replies/add/'+ obj.pno : '/replies/addReply/'+ obj.pno;
		
		$.ajax({
			type:'post',
			url: url,
			data:JSON.stringify(obj), 
			dataType:'json',
			contentType: "application/json",
			success:callback
		});
	};
	
	var update = function(obj, callback){
		console.log("update.......");
		debugger
		$.ajax({
			type:'put',
			url: '/replies/'+ obj.pno,
			dataType:'json',
			data: JSON.stringify(obj),
			contentType: "application/json",
			success:callback
		});
		
	};
	
	var remove = function(obj, callback){
		
		console.log("remove........");
		
		$.ajax({
			type:'delete',
			url: '/replies/'+ obj.pno+"/" + obj.rno,
			dataType:'json',
			data: JSON.stringify(obj),
			contentType: "application/json",
			success:callback,
			error:function(){
				alert("대댓글이 있는 경우 삭제할 수 없습니다.");
				return false;
			}
		});
	};
	
	return { 
		getAll: getAll,
		add:add,
		update:update,
		remove:remove,
		getMaxSeq : getMaxSeq
	}
		
})();

