import java.io.IOException;

/**
 * InfectStatistic
 * TODO
 *
 * @author 221701409_陈茜
 * @version 1.0
 * @since 
 */
class InfectStatistic {
	
	public static boolean verifyArgs(String[] arg) {//处理命令行参数
		
		return true;
	}
	public boolean readFileManager() throws IOException {//读取处理文件
		return true;
	}
	public void outputResult() throws IOException {//输出结果
		
	}
	public static void main(String[] args) throws IOException {
    	InfectStatistic infectStatistic = new InfectStatistic();
    	if (!verifyArgs(args) || !infectStatistic.readFileManager()) {
    		return ;
    	}
    	infectStatistic.outputResult();
    }
}
