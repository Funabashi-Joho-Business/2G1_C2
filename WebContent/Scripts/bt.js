document.addEventListener("DOMContentLoaded", Main, false);

function Main()
{
	var output = document.querySelector("div#output");
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
		output.innerHTML = data1.value + data2.value + data3.value + data4.value;
	
		//データ送信処理
		var sendData = {};
		sendData.cmd = "write";
		sendData.date = data1.value;
		sendData.Kategori = data2.value;
		sendData.Kingaku = data3.value;
		sendData.Gaiyou = data4.value;
		sendData.User = "x14g009";
		AFL.sendJson("table",sendData,onRecv);
		function onRecv(data){
			if(data)
				alert("送信成功");
			else
				alert("送信失敗");
		}

	}
}