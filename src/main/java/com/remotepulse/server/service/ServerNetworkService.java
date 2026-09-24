package com.remotepulse.server.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class ServerNetworkService {

    public String findLocalIpAddress() {

        try {

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-Command",
                            "(Get-NetIPConfiguration | " +
                            "Where-Object { " +
                            "$_.IPv4DefaultGateway -ne $null " +
                            "} | " +
                            "Select-Object -First 1 " +
                            "-ExpandProperty IPv4Address).IPv4Address"
                    );


            Process process =
                    processBuilder.start();


            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()
                            )
                    );


            String ipAddress =
                    reader.readLine();


            process.waitFor();


            if (ipAddress != null &&
                    !ipAddress.isBlank()) {

                return ipAddress.trim();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        return "Unknown";
    }
}