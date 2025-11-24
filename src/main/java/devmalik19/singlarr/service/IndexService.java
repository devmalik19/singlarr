package devmalik19.singlarr.service;

import devmalik19.singlarr.data.dao.Index;
import devmalik19.singlarr.repository.IndexRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexService
{
    @Autowired
	private IndexRepository indexRepository;

    public List<Index> findAll()
    {
        return indexRepository.findAll();
    }

}
