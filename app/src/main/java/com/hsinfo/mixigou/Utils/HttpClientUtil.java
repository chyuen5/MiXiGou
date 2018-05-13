package com.hsinfo.mixigou.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * http请求帮助类
 */
public class HttpClientUtil 
{
    public static String http_result_key = "HTTP.RESULT.VLUEKEY";
    public static String http_cookie_key = "HTTP.COOKIE";
    public static String http_cookie_count = "HTTP.COOKIE.COUNT";

	public static String DEFAULTENC = "UTF-8";
	private static final int REQUEST_TIMEOUT = 5 * 4000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 12000; // 设置等待数据超时时间10秒钟

	//返回Get的字符串
	public static String HttpUrlConnectionGet(String urlPath, String enc)
	{
		String strret = "";
		HttpURLConnection connection = null;
		InputStream is = null;
		int result = 0;
        try 
        {
        	//获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 默认为GET
            connection.setRequestMethod("GET");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            connection.connect();
            //相应码是否为200
            result = connection.getResponseCode();
            if( result== HttpURLConnection.HTTP_OK )
            {
            	//获得输入流
                is = connection.getInputStream();
                //包装字节流为字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    response.append(line);
                }
                return response.toString();
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (connection != null) 
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null) 
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strret;
	}

    //GET方法，带参数
    public static String HttpUrlConnectionGet2(String urlPath, Map<String, String> params, String enc)
    {
        int result = 0;
        String strret = "";
        String strurl1 = "";
        String strurl2 = "";
        HttpURLConnection connection = null;
        InputStream is = null;
        StringBuffer body = getParamString(params);
        strurl1 = urlPath+"?"+body.toString();

        try
        {
            //获得URL对象
            //URL url = new URL(urlPath);
            URL url = new URL(strurl1);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 默认为GET
            connection.setRequestMethod("GET");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(60*1000);
            //设置读取超时时间
            connection.setReadTimeout(60*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            //connection.setDoInput(true);

            //======
            //connection.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            //connection.setRequestProperty("Content-Encoding", "gzip");

            connection.connect();
            //相应码是否为200
            result = connection.getResponseCode();
            if( result== HttpURLConnection.HTTP_OK )
            {
                //获得输入流
                is = connection.getInputStream();
                //包装字节流为字符流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strret;
    }

    //Post方法
    public static String HttpUrlConnectionPost(String urlPath, Map<String, String> params, String enc)
    {
        int result = 1;
        String buffer;
        String strret = "";
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;

        StringBuffer body = getParamString(params);
        buffer = body.toString();
        buffer = "[{"+buffer+"}]";
        byte[] data = buffer.getBytes();

        if (params == null || params.size() == 0)
        {
            return strret;
        }

        try
        {
            //获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);

            //connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Type", "application/x-javascript text/xml");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Type", "application/x-javascript; charset=UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            OutputStream outStream = connection.getOutputStream();
            outStream.write(data);
            outStream.flush();
            outStream.close();
//            os = connection.getOutputStream();
//            os.write(data);
//            os.flush();
//            os.close();
            //写入参数
            result = connection.getResponseCode();
            if( result== HttpURLConnection.HTTP_OK )
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strret;
    }

	//Post方法
	public static String HttpUrlConnectionPost2(String urlPath, Map<String, String> params, String enc)
	{
		String strret = "";
		OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString(params);
        byte[] data = body.toString().getBytes();
		
		if (params == null || params.size() == 0) 
		{
			return strret;
        }
		
		try 
        {
			//获得URL对象
			URL url = new URL(urlPath);
			//获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);

            //connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            os = connection.getOutputStream();
            os.write(data);
            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (connection != null) 
            {
            	connection.disconnect();
            	connection = null;
            }
            if (is != null) 
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		return strret;
	}

    //Post方法
    public static String HttpUrlConnectionPost3(String urlPath, Map<String, String> params, String enc)
    {
        String strret = "";
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString2(params);
        byte[] data = body.toString().getBytes();

        if (params == null || params.size() == 0)
        {
            return strret;
        }

        try
        {
            //获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            os = connection.getOutputStream();
            os.write(data);
            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strret;
    }

    //车辆管理用
    public static String HttpUrlConnectionPost4(String urlPath, Map<String, String> params, String enc)
    {
        String strret = "";
        OutputStream outstream = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString(params);
        byte[] data = body.toString().getBytes();
        StringBuilder response = new StringBuilder();

        if (params == null || params.size() == 0)
        {
            return strret;
        }

        try
        {
            //获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            outstream = connection.getOutputStream();
            outstream.write(data);
            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                //StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
        //return strret;
    }

    //车辆管理用  Post方式传递参数
    public static String HttpUrlConnectionPost5(String urlPath, Map<String, String> params, String enc, int debug)
    {
        String strret = "";
        String resultData = "";
        OutputStream outStream = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString2(params);
        byte[] data = body.toString().getBytes();
        StringBuilder response = new StringBuilder();


        if (params == null || params.size() == 0)
        {
            return strret;
        }

        try
        {
            //获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);

            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            outStream = connection.getOutputStream();
            outStream.write(data);
            outStream.flush();
            outStream.close();
            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                //StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                strret = response.toString();
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //return response.toString();
        return strret;
    }

    public static String HttpUrlConnectionPost6(String urlPath, Map<String, String> params, String enc, int debug)
    {
        String strurl1;
        StringBuilder sb = new StringBuilder();
        StringBuffer body = getParamString(params);
        strurl1 = urlPath + "?" + body.toString();

        try {
            URL url = new URL(strurl1);
            InputStream in = url.openConnection().getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //StringBuilder sb = new StringBuilder();

            String line = null;
            try
            {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

	private static StringBuffer getParamString(Map<String, String> params)
	{
        StringBuffer result = new StringBuffer();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) 
        {
            Map.Entry<String, String> param = iterator.next();
            String key = param.getKey();
            String value = param.getValue();
            result.append(key).append('=').append(value);
            if (iterator.hasNext())
            {
                result.append('&');
            }
        }
        return result;
    }

    //输出的结构 {current_page:1}&{status:1}&{CurrentLoginUserAccount:chenyu2}
    private static StringBuffer getParamString2(Map<String, String> params)
    {
        StringBuffer result = new StringBuffer();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, String> param = iterator.next();
            String key = param.getKey();
            String value = param.getValue();
            result.append("{").append(key).append(':').append(value).append("}");
            if (iterator.hasNext())
            {
                result.append('&');
            }
        }
        return result;
    }

	//返回get的字符串
	public static int  httpPostWithJSON(String path, String jsonstr)
	{
        try
        {
            /*
             * 通过URL取得HttpURLConnection 
             * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置 
             * <uses-permission android:name="android.permission.INTERNET" /> 
             */
            URL url = new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(30*1000);  //设置连接超时
            conn.setRequestProperty("Content-Type", "application/x-javascript;chatset=utf-8");
            
            if( jsonstr.length()==0) return 3;
            {
            	PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
				out.write(jsonstr);
				out.close();
            }
            
            //取得inputStream，并进行读取
            InputStream input = conn.getInputStream();
            Scanner scanner = new Scanner(input, "UTF-8");
            String result = scanner.useDelimiter("\\A").next();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 4;
        }
        
        return 1;
	}

    public static String HttpUrlConnectionPostLog(String urlPath, String params)
    {
        String strret = "";
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        byte[] data = params.getBytes();

        if (params == null || params.length() == 0)
        {
            return strret;
        }

        try
        {
            //获得URL对象
            URL url = new URL(urlPath);
            //获得HttpURLConnection对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为post
            connection.setRequestMethod("POST");
            //不使用缓存
            connection.setUseCaches(false);
            //设置超时时间
            connection.setConnectTimeout(10*1000);
            //设置读取超时时间
            connection.setReadTimeout(10*1000);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置为true后才能写入参数
            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            os = connection.getOutputStream();
            os.write(data);

            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
                return response.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
                connection = null;
            }
            if (is != null)
            {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return strret;
    }
	
}