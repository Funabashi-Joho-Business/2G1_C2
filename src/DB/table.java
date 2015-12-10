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
	public String name;
	public String msg;
	public int id;
}

/**
 * Servlet implementation class Ajax10
 */
@WebServlet("/Ajax10")
public class table extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DB.table mOracle;
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


		try{
			mOracle = new Oracle();
			mOracle.connect("ux4", DB_ID, DB_PASS);

			//テーブルが無ければ作成
			if(!mOracle.isTable("db_exam10"))
			{
				mOracle.execute("create table db_exam10(日付 date,カテゴリ varchar2(20),概要 varchar2(80),金額 int )");
				mOracle.execute("create sequence db_exam10_seq");
			
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
		RecvData recvData = JSON.decode(request.getInputStream(),
				RecvData.class);
		if ("write".equals(recvData.cmd)) {
			// 書き込み処理
			String sql = String
					.format("insert into db_exam10 values(db_exam10_seq.nextval,'%s','%s')",
							recvData.Kategori, recvData.Gaiyou,recvData.Kingaku);
			mOracle.execute(sql);
		}

		try {
			// データの送信処理
			ArrayList<SendData> list = new ArrayList<SendData>();
			ResultSet res = mOracle
					.query("select * from db_exam10 order by id");
			while (res.next()) {
				SendData sendData = new SendData();
				sendData.id = res.getInt(1);
				sendData.name = res.getString(2);
				sendData.msg = res.getString(3);
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
