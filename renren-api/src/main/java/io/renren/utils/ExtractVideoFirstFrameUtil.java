//package io.renren.utils;
//
//
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.http.HttpUtil;
//import io.renren.oss.cloud.OSSFactory;
//import org.apache.commons.io.FilenameUtils;
//import org.bytedeco.javacpp.Loader;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Optional;
//
///** @Author huyi @Date 2021/11/11 11:08 @Description: 提取视频第一帧 */
//public class ExtractVideoFirstFrameUtil {
//
//    /**
//     * 提取主方法
//     *
//     * @param path MP4视频路径
//     * @param tmpDir 临时目录
//     * @return 视频第一帧
//     * @throws Exception 异常
//     */
//    public static String extract(String path, String tmpDir) throws Exception {
//        String videoName = IdUtil.simpleUUID();
//        String mp4Path = tmpDir + "/" + videoName + ".mp4";
//        HttpUtil.downloadFile(path, mp4Path);
//        String imgName = IdUtil.simpleUUID();
//        String imgPath = tmpDir + "/" + imgName + ".jpg";
//        String s = ffmpegExtractImage(mp4Path, imgPath)
//                .orElseThrow(() -> new Exception("提取失败"));
//        //上传到oss
//        //上传到阿里云OSS
//        MultipartFile file = new MockMultipartFile(imgName, new FileInputStream(s));
//        //上传文件
//        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
//        //拿到文件存储地址
//        String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);
//        //删除图片和视频
//        deleFile(mp4Path);
//        deleFile(imgPath);
//        return url;
//    }
//
//    public static void deleFile(String filpath){
//        //删除文件
//        try {
//            Path path = Paths.get(filpath);
//            Files.delete(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 提取视频第一帧图片
//     *
//     * @param mp4Path 视频地址
//     * @param picPath 图片地址
//     * @return 提取的图片地址
//     */
//    public static Optional<String> ffmpegExtractImage(String mp4Path, String picPath) {
//        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
//
//        ProcessBuilder extractBuilder =
//                new ProcessBuilder(
//                        ffmpeg, "-i", mp4Path, "-f", "image2", "-ss", "1","-frames:v", "1", picPath);
//        try {
//            extractBuilder.inheritIO().start().waitFor();
//        } catch (InterruptedException | IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//        // 返回图片文件路径
//        return Optional.of(picPath);
//    }
//
//    public static void main(String[] args) throws Exception {
//        String extract = extract("http://v26-default.douyinvod.com/6c593d8006e818e2bccd7417e442d373/64ad038c/video/tos/cn/tos-cn-ve-15c001-alinc2/okDaICSbrhXQUeA9NkpkQgewCw9nDVgAACLcNB/?a=0&ch=26&cr=0&dr=0&lr=all&cd=0%7C0%7C0%7C0&cv=1&br=1375&bt=1375&cs=0&ds=6&ft=TXW4TwMMUUmf.ud~D02D1YmAo6kItGalu7q9eFVRwH4D12nz&mime_type=video_mp4&qs=0&rc=NWc0PDZnaTVoMzM0ZjtkNEBpamU7Mzg6ZjZlbDMzNGkzM0A2YGEzNl5eXjAxMTBeNDQ0YSNkL2FocjRfbGdgLS1kLS9zcw%3D%3D&l=20230711142237A2A66B21D6800E1A5317&btag=e00028000", "/Users/shican/douyin/");
//        System.out.println(extract);
//    }
//}
