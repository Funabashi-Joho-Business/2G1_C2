package DB;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

class RecvData {
	public Date date;
	public int Kategori;
	public String Gaiyou;
	public int Kingaku;
	public String cmd;
	public String User;
}

class SendData {
	public Date date;
	public String User;
	public int Kategori;
	public String Gaiyou;
	public int Kingaku;
	public String cmd;
}

/**
 * Servlet implementation class Ajax10
 */
@WebServlet("/table")
public class table extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DB.Oracle mOracle;
	private final String DB_ID = "x14g019";
	private final String DB_PASS = "sakura39";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public table() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
		super.init();

		try {
			mOracle = new Oracle();
			mOracle.connect("ux4", DB_ID, DB_PASS);

			// テーブルが無ければ作成
			if (!mOracle.isTable("main_table")) {
				mOracle.execute("drop sequence Main_table_SeqID");
				mOracle.execute("create table main_table(レコード番号 int,ユーザID varchar2(10),日付 date,カテゴリ int,概要 varchar2(80),金額 int)");
				mOracle.execute("create sequence main_table_SeqID");
			}
			//カテゴリテーブル作成
			if (!mOracle.isTable("Kategori_table")) {
				mOracle.execute("drop sequence Kategori_table_SeqID");
				mOracle.execute("create table Kategori_table(カテゴリID　int,ユーザID varchar2(10),カテゴリ名  varchar2(20))");
				mOracle.execute("create sequence Kategori_table_SeqID");
				//デフォルトで存在するカテゴリを入れる
				mOracle.execute("insert into Kategori_table values(Kategori_table_SeqID.nextval,'default','電気代')");
				mOracle.execute("insert into Kategori_table values(Kategori_table_SeqID.nextval,'default','水道代')");
				mOracle.execute("insert into Kategori_table values(Kategori_table_SeqID.nextval,'default','ガス代')");
				mOracle.execute("insert into Kategori_table values(Kategori_table_SeqID.nextval,'default','遊び')");
				mOracle.execute("insert into Kategori_table values(Kategori_table_SeqID.nextval,'default','食事')");
			}
		} catch (Exception e) {
			System.err.println("認証に失敗しました");
		}
	}
	void insertData(String user,Date hiduke, int kategori, String gaiyou, int kingaku){
		String sql;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		String dateStr = format.format(hiduke);		
		sql = String.format("insert into main_table values(main_table_SeqID.nextval,'%s',to_date('%s','yyyy/mm/dd hh24:mi:ss'),%d,'%s',%d)",
				user,dateStr,kategori,gaiyou,kingaku);
		System.out.println(sql);
		mOracle.execute(sql);
	}
	@Override
	public void destroy() {
		// DB切断
		mOracle.close();
		// TODO 自動生成されたメソッド・スタブ
		super.destroy();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		action(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		action(request, response);
	}

	private void action(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		// 出力ストリームの作成
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			// データの受け取り処理
			RecvData recvData = JSON.decode(request.getInputStream(),
					RecvData.class);
			if ("write".equals(recvData.cmd)) {
				// 書き込み処理
				//サンプル
				insertData(recvData.User,recvData.date,recvData.Kategori,recvData.Gaiyou,recvData.Kingaku);	
			}

			// データの送信処理
			ArrayList<SendData> list = new ArrayList<SendData>();
			ResultSet res = mOracle
					.query("select * from main_table order by 日付 desc");
			while (res.next()) {
				SendData sendData = new SendData();
				sendData.date = res.getDate(1);
				sendData.User = res.getString(2);
				sendData.Kategori = res.getInt(3);
				sendData.Gaiyou = res.getString(4);
				sendData.Kingaku = res.getInt(5);
				sendData.cmd = res.getString(6);
				list.add(sendData);
			}
			// JSON形式に変換
			String json = JSON.encode(list);
			// 出力
			out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
