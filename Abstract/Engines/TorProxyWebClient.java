package Abstract.Engines;

import Services.DIResolver;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

public class TorProxyWebClient extends ProxyWebClient {
    public TorProxyWebClient(DIResolver diResolver) {
        super(diResolver);
    }

    @Override
    protected  CloseableHttpResponse execute(HttpUriRequest request, String country) throws IOException {
        return TorProxyContext.execute(request);
    }
}
