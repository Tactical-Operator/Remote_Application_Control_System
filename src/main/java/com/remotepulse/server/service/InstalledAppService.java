package com.remotepulse.server.service;

import com.remotepulse.server.model.InstalledApp;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstalledAppService {

    public List<InstalledApp> getInstalledApps(){

        List<InstalledApp> apps = new ArrayList<>();

        try{

            ProcessBuilder processBuilder = new ProcessBuilder(
                "Powershell.exe",
                "-NoProfile",
                "-Command",
                "Get-StartApps | ForEach-Object { $_.Name + [char]9 + $_.AppID }"
            );

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line;

            while((line = reader.readLine()) != null){

                if(line.isBlank()){
                    continue;
                }

                String[] parts = line.split("\t",2);

                if(parts.length ==2){

                    String name = parts[0].trim();
                    String appId = parts[1].trim();

                    apps.add(new InstalledApp(name, appId));
                }
            }
            process.waitFor();
        } catch(Exception e){
            e.printStackTrace();
        }
        return apps;
    }

}
