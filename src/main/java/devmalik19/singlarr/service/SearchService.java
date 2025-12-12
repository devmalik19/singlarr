package devmalik19.singlarr.service;

import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.service.external.ProwlarrService;
import devmalik19.singlarr.service.plugins.SlskdService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchService
{
    @Autowired
    private ProwlarrService prowlarrService;

    @Autowired
    private SlskdService slskdService;

    public Page<SearchResult> searchProwlarr(String searchTerm, Pageable pageable) throws Exception
    {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		List<SearchResult> searchResults = Arrays.asList(prowlarrService.search(searchTerm));
		searchResults = sortResults(pageable, searchResults);

		List<SearchResult> pageContent;
		if (searchResults.size() < startItem)
		{
			pageContent = Collections.emptyList();
		}
		else
		{
			int toIndex = Math.min(startItem + pageSize, searchResults.size());
			pageContent = searchResults.subList(startItem, toIndex);
		}

		return new PageImpl<>(pageContent, PageRequest.of(currentPage, pageSize), searchResults.size());
    }

	private static List<SearchResult> sortResults(Pageable pageable, List<SearchResult> searchResults)
	{
		Sort sort = pageable.getSort();
		if (sort.isSorted() && !searchResults.isEmpty())
		{
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();

			Comparator<SearchResult> comparator = null;
			switch (property)
			{
				case "protocol": comparator = Comparator.comparing(SearchResult::getProtocol, Comparator.nullsLast(String::compareTo)); break;
				case "indexer":  comparator = Comparator.comparing(SearchResult::getIndexer, Comparator.nullsLast(String::compareTo));  break;
				case "title":    comparator = Comparator.comparing(SearchResult::getTitle, Comparator.nullsLast(String::compareTo));    break;
				case "seeders":  comparator = Comparator.comparingInt(SearchResult::getSeeders);                                      break;
				case "leechers": comparator = Comparator.comparingInt(SearchResult::getLeechers);                                      break;
			}

			if (comparator != null)
			{
				if (order.isDescending())
				{
					comparator = comparator.reversed();
				}
				searchResults = searchResults.stream().sorted(comparator).collect(Collectors.toList());
			}
		}
		return searchResults;
	}
}
