/**
 *  This class is to make HTTP requests (GET, POST) using spring boot RESTClient library.
 */
package devmalik19.singlarr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class HttpRequestService
{
	Logger logger = LoggerFactory.getLogger(HttpRequestService.class);

	/**
	 *  This method is used to send a GET request with headers.
	 *
	 * @param url  Url in string format.
	 * @param headers A hashmap of headers with key and value as header key and value.
	 * @return It will return in same class type object that you pass in cls param.
	 *
	 */
	public String doGetRequest(String url, Map<String, String> headers)
	{
		return doGetRequest(url, headers, new HashMap<>());
	}

	/**
	 *  This method is used to send a GET request without any headers or query string.
	 *
	 * @param url  Url in string format.
	 * @return It will return in same class type object that you pass in cls param.
	 *
	 */
	public String doGetRequest(String url)
	{
		return doGetRequest(url, new HashMap<>(), new HashMap<>());
	}

	/**
	 *  This method is used to send a GET request with headers and query string.
	 *
	 * @param url  Url in string format.
	 * @param headers A hashmap of headers with key and value as header key and value.
	 * @param params Query params as key and value of hashmap, we will build the query like this &key=value
	 * @return It will return in same class type object that you pass in cls param.
	 *
	 */
    public String doGetRequest(String url,
							  Map<String, String> headers,
							  Map<String, String> params)
    {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
		params.forEach(uriBuilder::queryParam);
		URI finalUri = uriBuilder.build().toUri();

        RestClient restClient = RestClient.create();
		try
		{
			return restClient
					.get()
					.uri(finalUri)
					.headers(httpHeaders -> headers.forEach(httpHeaders::add))
					.retrieve()
					.body(String.class);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 *  This method is used to send a POST request.
	 *
	 * @param url Url in string format.
	 * @param body Body of the request.
	 * @param headers A hashmap of headers with key and value as header key and value.
	 * @return It will return in same class type object that you pass in cls param.
	 *
	 */
    public String doPostRequest(String url, String body, Map<String, String> headers)
    {
        RestClient restClient = RestClient.create();
        return restClient
                .post()
                .uri(url)
                .body(body)
				.headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .body(String.class);
    }
}
