import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
/**
 * InfectStatistic
 * TODO
 *
 * @author 221701409_陈茜
 * @version 1.0
 * @since 
 */
class InfectStatistic {
    private String[] args;//命令行参数
    private static String date;//日志日期
    private static String logPath; //日志路径
    private static String outPath;//output文件路径
    private static String[] type;//输出类型
    private static List<String> province;//需输出的省份
    private static String[] allProvinceList;//所有省份名称
    private static boolean allType;//输出所有type
    private static boolean allProvince;//输出所有province
    private HashMap<String,Provinces> map;//参数province的映射关系
    
    public InfectStatistic(){
    	allProvinceList = new String[]{"全国","安徽","北京","重庆","福建","甘肃","广东",
    		"广东","广西","贵州","海南","河北","河南","黑龙江","湖北","湖南","江西",
    		"吉林","江苏","辽宁","内蒙古","宁夏","青海","山西","山东","陕西","上海",
    		"四川","天津","西藏","新疆","云南","浙江"};
    	type = new String[10];
    	allType = true;
    	allProvince = true;
        province = new ArrayList<>();
        map = new HashMap<String,Provinces>();
        for (int i = 0; i < allProvinceList.length; i ++ )
        	map.put(allProvinceList[i], null);
    }
    
	public static void main(String[] args) throws IOException {//主函数
    	InfectStatistic infectStatistic = new InfectStatistic();
    	if (!verifyArgs(args) || !infectStatistic.readFileManager()) {//args和cmd测试用
    		return ;
    	}
    	infectStatistic.outputResult();
    	System.out.println("执行成功！");
    }
	
	public static boolean verifyArgs(String[] args) {//处理命令行参数
		if(args[0].equals("list")){
    		for (int i = 1;args[i] != null &&  i < args.length; i++) {
    			i++;
    			switch (args[i-1]) {
					case "-date":
						date = new String(args[i]);
	                    break;
    				case "-log":
    					logPath = args[i];
                        break;
    				case "-out":
    					outPath = args[i];
                        break;
    				case "-type":
    					allType = false;
    					for(int j = 0;!args[i].startsWith("-");j++,i++) {
    						if(args[i].equals("ip")||args[i].equals("sp")||args[i].equals("cure")||args[i].equals("dead")) {
    							type[j] = args[i];
    						}
    						else {
    							System.out.println("type参数值错误");
    							return false;
    						}
    					}
    					i--;
                        break;
    				case "-province":
    					allProvince = false;
    					boolean b = false;
    					while(i < args.length && !args[i].startsWith("-")) {
    						for(int k = 0;k < allProvinceList.length;k++) {
    							if(allProvinceList[k].equals(args[i])) {
        							province.add(args[i]);
        							b = true;
        							break;
        						}
    						}
    						if(!b) {
    							System.out.println("province参数值错误");
    							return false;
    						}
    						i++;
    					}
    					i--;
                        break;
    				default:
                           return false;
                }
            }
    		return true;
        }
    	return false;
	}
	
	public boolean readFileManager() throws IOException {//读取处理文件
		boolean b = false;
        String fileName;
        String[] fileDate;
        int i,j;
        File file = new File(logPath);
        File[] tempList = file.listFiles();      
        for(i = 0;i < tempList.length;i++) {
        	fileName = new String(tempList[i].getName()); 
        	fileDate = fileName.split("\\."); 
        	if(date.compareTo(fileDate[0]) >= 0) {
        		//System.out.println(fileDate[0]);
        		BufferedReader br = null;               
                String dates = null;
                br = new BufferedReader(new InputStreamReader(new FileInputStream(tempList[i].toString()), "UTF-8"));  
                while ((dates = br.readLine()) != null) {
                	String[] date = dates.split(" ");
                    if (map.get(date[0]) == null)
                        map.put(date[0], new Provinces(date[0]));
                    else if (date[0].equals("//"))
            	        continue;
                    //System.out.println(dates);
                    switch(date[1]) {
                        case "新增":
                            if (date[2].equals("感染患者")){
                                map.get(date[0]).add_Ip(date[3]);
                            }
                            else{
                                map.get(date[0]).add_Sp(date[3]);
                            }
                            break;
                        case "疑似患者":
                            if (date[2].equals("流入")) {
                                map.get(date[0]).reduce_Sp(date[4]);
                                if (map.get(date[3]) == null)
                                    map.put(date[3], new Provinces(date[3]));
                                map.get(date[3]).add_Sp(date[4]);
                            }
                            else{
                            	map.get(date[0]).reduce_Sp(date[3]);
                                map.get(date[0]).add_Ip(date[3]);
                            }
                            break;
                        case "感染患者":
                            map.get(date[0]).reduce_Ip(date[4]);
                            if (map.get(date[3]) == null)
                                map.put(date[3], new Provinces(date[3]));
                            map.get(date[3]).add_Ip(date[4]);
                            break;
            	        case "排除": map.get(date[0]).Exclude(date[3]); break;
            	        case "死亡": map.get(date[0]).dead(date[2]); break;
            	        case "治愈": map.get(date[0]).cure(date[2]); break;
                        default: break;
                    }
                }
                b = true;
                br.close();
        	}
        }
        //map.get("福建").output();
        //map.get("湖北").output();
        if (map.get(allProvinceList[0]) == null)//对每个日志文件统计全国信息
        	map.put(allProvinceList[0], new Provinces(allProvinceList[0]));
        for (i = 0; i < allProvinceList.length; i ++ ){
            if (map.get(allProvinceList[i]) != null) {
            	//System.out.print(allProvinceList[i]);
            	map.get(allProvinceList[0]).Statistics(map.get(allProvinceList[i]));
            }
        }
        return b;
	}
	
	public void outputResult() throws IOException {//输出结果
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8"));//默认输出所有省情况(无-province参数)
        if (allProvince) {
            for (int i = 0;i < allProvinceList.length;i++)
                if (map.get(allProvinceList[i]) != null)
                    map.get(allProvinceList[i]).output(allType,type ,bw);
        }
        else {
            for (int i = 0; i < allProvinceList.length; i ++ )
                if (province.contains(allProvinceList[i]))
                    map.get(allProvinceList[i]).output(allType,type ,bw);
        }
        bw.write("// 该文档并非真实数据，仅供测试使用");
        bw.close();
	}
}

class Provinces {
	private String provincename;//省份名
	private int sp;	//疑似病例人数
	private int ip;	//感染病例人数
	private int cure;//治愈病例人数
	private int dead;//死亡病例人数
	
	public Provinces(String name){
		provincename = name;
		sp = 0;
		ip = 0;
		cure = 0;
		dead = 0;
	}
	
	

	public void Statistics(Provinces p){//统计全国数据
	    ip += p.ip;
	    sp += p.sp;
	    cure += p.cure;
	    dead += p.dead;
	    //System.out.println(p.ip+" "+p.sp+" "+p.cure+" "+p.dead);
	    //System.out.println("全国 "+ip+" "+sp+" "+cure+" "+dead);
	}

	public void output(boolean alltype, String[] type, BufferedWriter bw) throws IOException{//输出省的情况，默认输出所有type类型（无-type参数）
	    if (alltype) {
	        bw.write(provincename+ " 感染患者 " +ip+ "人 疑似患者 " +sp+ "人  治愈 " 
	        		+cure+ "人 死亡 " +dead+ "人");
	        bw.newLine();
	    }
	    else {
	          bw.write(provincename);
	          for (int i = 0;i < type.length && type[i] != null; i ++ ) {
	        	  switch (type[i]) {
	        	  	case "ip": bw.write(" 感染患者" +ip+ "人"); break;
	                case "sp": bw.write(" 疑似患者" +sp+ "人"); break;
	                case "cure": bw.write(" 治愈" +cure+ "人"); break;
                    case "dead": bw.write(" dead" +dead+ "人"); break;
                    default: break;
	                }
	          }
	          bw.newLine();
	      }       
	  }
	
//	public void output() {
//	System.out.println(ip+" "+sp+" "+cure+" "+dead);
//}
}