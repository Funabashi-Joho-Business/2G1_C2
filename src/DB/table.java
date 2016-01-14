package DB;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

class RecvData {
	public String Kategori;
	public String Gaiyou;
	public int Kingaku;
	public int cmd;
}

class SendData {
	public String Kategori;
	public String Gaiyou;
	public int Kingaku;
	public int cmd;
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
			System.out.println("テーブル作成中");
			mOracle.connect("ux4", DB_ID, DB_PASS);

			// テーブルが無ければ作成
			if (!mOracle.isTable("main_table")) {
				mOracle.execute("create table main_table(レコード番号 int, ユーザID varchar2(10) ,日付 date,カテゴリ int,概要 varchar2(80),金額 int)");
				mOracle.execute("create sequence main_table_SeqID");
			}
			
		} catch (Exception e) {
			System.err.println("認証に失敗しました");
		}
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

		// データの受け取り処理
		System.out.println("データ受け取り");
		RecvData recvData = JSON.decode(request.getInputStream(),
				RecvData.class);
		if ("write".equals(recvData.cmd)) {
			// 書き込み処理
		System.out.println("データ書き込み");
			String sql = String
					.format("insert into main_table values(main_table_seq.nextval,'%s','%s')",
							recvData.Kategori, recvData.Gaiyou,
							recvData.Kingaku);
			mOracle.execute(sql);
		}

		try {
			// データの送信処理
			System.out.println("データ送信");
			ArrayList<SendData> list = new ArrayList<SendData>();
			ResultSet res = mOracle
					.query("select * from main_table order by id");
			while (res.next()) {
				SendData sendData = new SendData();
				sendData.Kategori = res.getString(1);
				sendData.Gaiyou = res.getString(2);
				sendData.Kingaku = res.getInt(3);
				sendData.cmd = res.getInt(4);
				list.add(sendData);
			}
			// JSON形式に変換
			String json = JSON.encode(list);
			// 出力
			out.println(json);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
