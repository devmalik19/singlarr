package devmalik19.singlarr.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

@Service
public class FileSystemService
{
    public void getAllFilesList(String path)
    {
        Path start = Paths.get(path);
        try
        {
            Files.walk(start).forEach(System.out::println);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
