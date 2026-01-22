let currentPage = 0;
let pageSize = 100;
let currentSortField = '';
let currentSortDirection = '';
let currentSearchTerm = '';

async function metadataSearch(searchButton)
{
	const originalText = searchButton.textContent;
 	searchButton.disabled = true;
    searchButton.textContent = '...';

	const searchInput = document.getElementById("search").value;
	const artistInput = document.getElementById("artist").value;
	const albumInput = document.getElementById("album").value;
	const yearInput = document.getElementById("year").value;


	const sortParam = currentSortField ? `${currentSortField},${currentSortDirection}` : '';
	const params = new URLSearchParams({
        search: searchInput,
        artist: artistInput,
        album: albumInput,
        year: yearInput,
        page: currentPage,
		size: pageSize,
		sort: sortParam
	});

	url = "home/search?" + params.toString();
    const response = await doGetRequest(url);
    document.getElementById("searchResults").innerHTML = response;
    searchButton.textContent = originalText;
	searchButton.disabled = false;
}

async function triggerSearch(searchButton, id)
{
    const originalText = searchButton.textContent;
    searchButton.disabled = true;
    searchButton.textContent = '...';

    const response = await doGetRequest("search/trigger/"+id);

    if (response.ok)
    {
        searchButton.textContent = '✅';
    }
    else
    {
        searchButton.textContent = '❌';
    }

    searchButton.disabled = false;
}

async function interactiveSearch(searchButton, id)
{
	const originalText = searchButton.textContent;
 	searchButton.disabled = true;
    searchButton.textContent = '...';

	const sortParam = currentSortField ? `${currentSortField},${currentSortDirection}` : '';
	const params = new URLSearchParams({
		id: id,
		page: currentPage,
		size: pageSize,
		sort: sortParam
	});

	url = "search/interactive?" + params.toString();
    const response = await doGetRequest(url);
    document.getElementById("searchResults").innerHTML = response;
    searchButton.textContent = originalText;
	searchButton.disabled = false;
}

async function fetchPage(pageNumber)
{
    currentPage = pageNumber;
    fetchResults();
}

async function sortResults(header)
{
	field = header.getAttribute('data-header');
	if (currentSortField === field)
	{
        currentSortDirection = (currentSortDirection === 'asc') ? 'desc' : 'asc';
    }
    else
    {
        currentSortField = field;
        currentSortDirection = 'asc';
    }

    currentPage = 0;
    fetchResults();
}

async function fetchResults()
{
	const searchInput = document.getElementById("search");

	const sortParam = currentSortField ? `${currentSortField},${currentSortDirection}` : '';
	const params = new URLSearchParams({
		search: searchInput.value,
		page: currentPage,
		size: pageSize,
		sort: sortParam
	});

    const endpoint = document.getElementById("endpoint");
    uri = endpoint.getAttribute('data-endpoint');
	url = uri + "?" + params.toString();
    const response = await doGetRequest(url);
    document.getElementById("searchResults").innerHTML = response;
}

async function download(button)
{
    const downloadUrl = button.getAttribute('data-url');
    const protocol = button.getAttribute('data-protocol');
    const body = {
            url: downloadUrl,
            protocol: protocol
    };
    const response = await doPostRequestWithBody("search/download", body);
}

async function deleteSearch(id)
{
	if (confirm("Are you sure you want to delete this entry?"))
	{
		window.location.href = `delete?id=${id}`;
	}
}

function openDialog(button)
{
    const payload	=	button.getAttribute('data-payload');

    const modal	=	document.getElementById('searchModal');
    modal.dataset.pendingRow = payload;
    modal.showModal();
}

async function addSearch(button)
{
	const modal = document.getElementById('searchModal');
    const payload = modal.dataset.pendingRow;
    const data = JSON.parse(payload);

    const library = button.getAttribute('data-item');
    data.library = library;
    const response = await doPostRequestWithBody("search/add", data);
    modal.close();
}

