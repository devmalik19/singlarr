package devmalik19.singlarr.constants;

public enum Protocol
{
    TORRENT,
    USENET;

    public static boolean isTorrent(String value)
    {
        return value.toUpperCase().equals(Protocol.TORRENT.name());
    }

    public static boolean isUsenet(String value)
    {
        return value.toUpperCase().equals(Protocol.USENET.name());
    }


}
