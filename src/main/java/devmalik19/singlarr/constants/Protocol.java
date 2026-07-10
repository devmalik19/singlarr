package devmalik19.singlarr.constants;

public enum Protocol
{
    TORRENT,
    USENET;

    public static boolean isTorrent(String value)
    {
        return TORRENT.name().equalsIgnoreCase(value);
    }

    public static boolean isUsenet(String value)
    {
        return USENET.name().equalsIgnoreCase(value);
    }


}
