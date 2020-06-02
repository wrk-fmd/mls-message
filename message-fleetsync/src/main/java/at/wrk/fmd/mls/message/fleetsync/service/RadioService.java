package at.wrk.fmd.mls.message.fleetsync.service;

public interface RadioService {

    void handleIncomingMessage(String channel, String sender, boolean emergency);

    void reloadPorts();
}
