package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Protocol;
import devmalik19.singlarr.data.dto.DownloadRequest;
import devmalik19.singlarr.service.thirdparty.QbittorrentService;
import devmalik19.singlarr.service.thirdparty.SabnzbdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadService
{
    Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private QbittorrentService qbittorrentService;

    @Autowired
    private SabnzbdService sabnzbdService;

    public void download(DownloadRequest downloadRequest) throws Exception
    {
        if(Protocol.isTorrent(downloadRequest.getProtocol()))
            qbittorrentService.addTorrent(downloadRequest.getUrl());
        else
            sabnzbdService.addNzb(downloadRequest.getUrl());
    }
}
