document.addEventListener("DOMContentLoaded", Main, false);

function Main()
{
	var output = document.querySelector("div#output");
	var data1 = document.querySelector("input#day");
	var data2 = document.querySelector("select#Cat");
	var data3 = document.querySelector("input#price");
	var data4 = document.querySelector("input#syousai");
	var button = document.querySelector("input#bt");

	button.addEventListener("click",onClick);
	function onClick()
	{
		output.innerHTML = "bb"+data1.value + data2.value + data3.value + data4.value;
	}
}