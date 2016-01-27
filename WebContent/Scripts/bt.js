document.addEventListener("DOMContentLoaded", Main, false);

function Main()
{
	var output = document.querySelector("table#output");
	var data1 = document.querySelector("input#day");
	var data2 = document.querySelector("select#Cat");
	var data3 = document.querySelector("input#price");
	var data4 = document.querySelector("input#syousai");
	var button = document.querySelector("input#bt");

	//カレンダー系処理
	data1.addEventListener("click",onClickDate);
	function onClickDate()
	{
		//カレンダーの作成
		var cal = WM.createCalendarView();	
		cal.setDate(new Date());
		//カレンダーをフレームウインドウ化
		var frame = WM.createFrameWindow();
		frame.addChild(cal);
		frame.setSize(290,250);
		frame.setTitle("カレンダー");
		frame.setPos();

		//日付選択イベントの処理
		cal.addEvent("onDay",onDay);
		function onDay(param)
		{
			var d = param.date;
			data1.value = AFL.sprintf("%04d-%02d-%02d",
					d.getFullYear(),d.getMonth()+1,d.getDate());	
			frame.close();
		}
	}	
	
	button.addEventListener("click",onClick);
	function onClick()
	{
		//データ送信処理
		var sendData = {};
		sendData.cmd = "write";
		sendData.date = data1.value;
		sendData.Kategori = data2.value;
		sendData.Kingaku = data3.value;
		sendData.Gaiyou = data4.value;
		sendData.User = "x14g009";
		AFL.sendJson("table",sendData,onRecv);
	}
	
	var sendData = {};
	sendData.cmd = "read";
	AFL.sendJson("table",sendData,onRecv);

	function onRecv(datas){
		if(datas){
//			public Date date;
//			public int Kategori;
//			public String Gaiyou;
//			public int Kingaku;
//			public String cmd;
//			public String User;
			//テーブルのクリア
			if(output.rows.length > 1)
				output.deleteRow(1);
			for(var index in datas){
				var data = datas[index];
				var row = output.insertRow(-1);
				
				var date = new Date(data.date);
				var y = date.getFullYear();
				var m = date.getMonth()+1;
				var d = date.getDate();
				
				var cell;
				cell = row.insertCell(-1);
				cell.innerHTML = AFL.sprintf("%d年%02d月%02d日",y,m,d);
				cell = row.insertCell(-1);
				cell.innerHTML = data.Kategori;
				cell = row.insertCell(-1);
				cell.innerHTML = data.Kingaku + "円";
				cell = row.insertCell(-1);
				cell.innerHTML = data.Gaiyou;

				//alert(d.Kingaku);
			}
		}
		else
			alert("送信失敗");
	}	
}