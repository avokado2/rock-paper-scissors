function onChatMessageClick() {
  var el = document.getElementById('chat-message-input');
  if (!el.value) {
    return false;
  }
  fetch('/chat/add-message', {
      method: 'POST',
      headers: {
        'Accept': 'application/json, text/plain, */*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({gameId: 0, message: el.value})
    }).then(res => res.json())
      .then(res => {
        console.log(res);
        if (res.status == 'ok')  {
          el.value = '';
        } else {
          // TODO: show res.errorMessage inside chat
        }
      });
  return false;
}
function addChatMessage(chatMessage) {
  var elMessages = document.getElementById('chat-messages');
  var chatMessage = document.createElement('div');
  chatMessage.className='chat-message';
  var chatMessageNickname = document.createElement('span');
  chatMessageNickname.className='chat-message-nickname';
  chatMessageNickname.innerText= chatMessage.nickname + ': ';
  chatMessage.appendChild(chatMessageNickname);
  var txt = document.createTextNode(chatMessage.text);
  chatMessage.appendChild(txt);
  elMessages.insertBefore(chatMessage, elMessages.firstChild);
}
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket'
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/chat-messages/0', (chatMessage) => {
        console.log(chatMessage);
        addChatMessage(chatMessage);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};
stompClient.activate();
//var messageInput = document.getElementById("chat-message-input");
//
//messageInput.addEventListener("keypress", function(event) {
//  if (event.keyCode === 13) { // Код клавиши Enter
//    onChatMessageClick();
//  }
//});

