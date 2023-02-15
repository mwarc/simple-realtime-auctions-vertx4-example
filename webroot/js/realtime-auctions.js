const auction_id = 1;

function init() {
    loadCurrentPrice();
    registerHandlerForUpdateCurrentPriceAndFeed();
}

function loadCurrentPrice() {
    fetch("http://localhost:8080/api/auctions/" + auction_id)
        .then(response => {
            if (response.ok) {
                return response.json()
            }
            throw new Error('Something went wrong');

        })
        .then(response => document.getElementById('current_price').innerHTML = 'EUR ' + response.price.toFixed(2))
        .catch(() => document.getElementById('current_price').innerHTML = 'EUR 0.00');
}

function registerHandlerForUpdateCurrentPriceAndFeed() {
    const eventBus = new EventBus('http://localhost:8080/eventbus');
    eventBus.onopen = function () {
        eventBus.registerHandler('auction.' + auction_id, function (error, message) {
            document.getElementById('current_price').innerHTML = 'EUR ' + JSON.parse(message.body).price;
            document.getElementById('feed').value += 'New offer: EUR ' + JSON.parse(message.body).price + '\n';
        });
    }
}

function bid() {
    const newPrice = parseFloat(Math.round(document.getElementById('my_bid_value').value.replace(',', '.') * 100) / 100).toFixed(2);

    fetch("http://localhost:8080/api/auctions/" + auction_id, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({price: newPrice})
    })
        .then(response => {
            if (response.ok) {
                document.getElementById('error_message').innerHTML = '';
            } else {
                document.getElementById('error_message').innerHTML = 'Invalid price!'
            }
        })
        .catch(() => document.getElementById('error_message').innerHTML = 'Something went wrong')
}