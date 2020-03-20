package jp.co.canon.cks.eec.fs.rssportal.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.canon.cks.eec.fs.rssportal.Defines.Genre;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GenreController {
    @GetMapping("/getGenre")
    public Genre[] getGenre() {
      
        String convStr = null;
        try {
            // 바이트 단위로 파일읽기
            String filePath = "dataFile/genreList/genre.json"; // 대상 파일
            FileInputStream fileStream = null; // 파일 스트림
             
            fileStream = new FileInputStream( filePath );// 파일 스트림 생성
            //버퍼 선언
            byte[ ] readBuffer = new byte[fileStream.available()];
            while (fileStream.read( readBuffer ) != -1){}
            //System.out.println(new String(readBuffer)); //출력
            convStr = new String(readBuffer);
 
            fileStream.close(); //스트림 닫기
         } catch (Exception e) {
         e.getStackTrace();
         }

         System.out.print(convStr);

        // convert type to json class
        Gson gson = new Gson();
        Genre[] genreClass = gson.fromJson(convStr, Genre[].class);

        
        return genreClass;
    }

    @PostMapping("/setGenre")
    public String setGenre(@RequestBody String param) throws IOException {

        System.out.println("/setGenre");
        System.out.println(param);

        String result = "Success";

        Gson gson = new Gson();
        Genre[] convClass = gson.fromJson(param, Genre[].class);

        Gson gsonOut = new GsonBuilder().setPrettyPrinting().create();
        String convJson = gsonOut.toJson(convClass);
        System.out.println(convJson);


        // 파일 폴더(디렉토리)를 다루기 위한 객체
		File dir = new File("dataFile/genreList");

        // exists(): 파일, 폴더(디렉토리)가 존재하는 지(true) 아닌지를 리턴
		if (!dir.exists()) {// 파일, 폴더가 없는 경우

			System.out.println("폴더가 없습니다...");
			// mkdir(): 디렉토리를 생성하는 메소드, 생성 성공하면 true를 리턴
			if (dir.mkdirs()) {
				System.out.println("폴더 생성 성공");
			} else {
				System.out.println("폴더 생성 실패");
			}
		} else { // 파일, 폴더 있는 경우
			System.out.println("폴더가 이미 존재합니다");
		}

        BufferedOutputStream bs = null;
        try {
            bs = new BufferedOutputStream(new FileOutputStream("dataFile/genreList/genre.json"));
            bs.write(convJson.getBytes()); //Byte형으로만 넣을 수 있음

        } catch (Exception e) {
            e.getStackTrace();
            result = "Fail";

        }finally {
            bs.close(); //반드시 닫는다.
        } 
   
        return result;
    }
}