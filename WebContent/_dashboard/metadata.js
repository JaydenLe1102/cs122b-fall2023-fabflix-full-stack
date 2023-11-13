function handleMetaData(resultData) {
	console.log('Getting metadata: ' + JSON.stringify(resultData, null, 2))
	// Your metadata array

	const metadata = resultData

	// Create an object to store tables and their columns
	let tables = {}

	// Group columns by table name
	metadata.forEach(function (item) {
		let tableName = item.table_name
		if (!tables[tableName]) {
			tables[tableName] = []
		}
		tables[tableName].push(item)
	})

	// Render metadata by table
	for (let tableName in tables) {
		let tableData = tables[tableName]

		let tableHTML = '<div class="table-container">'

		tableHTML += '<h2>' + tableName + '</h2>'
		tableHTML += '<table>'
		tableHTML += '<tr><th>Column Name</th><th>Data Type</th></tr>'

		tableData.forEach(function (item) {
			tableHTML +=
				'<tr><td>' +
				item.column_name +
				'</td><td>' +
				item.data_type +
				'</td></tr>'
		})

		tableHTML += '</table>'

		tableHTML += '</div>'
		$('#metadata-container').append(tableHTML)
	}
}

function handleLoggedIn(resultData, callback) {
	console.log(resultData)
	console.log('User is logged in')
	if (resultData['isLoggedIn'] === true) {
		jQuery.ajax({
			dataType: 'json', // Setting return data type
			method: 'GET', // Setting request method
			url: 'api/metadata', // Setting request url, which is mapped by StarsServlet in Stars.java
			success: (resultData) => {
				callback(resultData)
			},
		})
	} else {
		console.log('User is not logged in')
		window.location.replace('loginForm.html')
	}
}

//perform browsing for the page
jQuery.ajax({
	dataType: 'json', // Setting return data type
	method: 'GET', // Setting request method
	url: 'api/login', // Setting request url, which is mapped by StarsServlet in Stars.java
	success: (resultData) => handleLoggedIn(resultData, handleMetaData), // Setting callback function to handle data returned successfully by the StarsServlet
})
