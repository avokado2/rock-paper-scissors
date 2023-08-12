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
          addChatMessageError(res.errorMessage);
        }
      });
  return false;
}
function addChatMessage(msg) {
  var elMessages = document.getElementById('chat-messages');
  var chatMessage = document.createElement('div');
  chatMessage.className='chat-message';
  var chatMessageNickname = document.createElement('span');
  chatMessageNickname.className='chat-message-nickname';
  chatMessageNickname.innerText= msg.nickname + ': ';
  chatMessage.appendChild(chatMessageNickname);
  var txt = document.createTextNode(msg.text);
  chatMessage.appendChild(txt);
  elMessages.insertBefore(chatMessage, elMessages.firstChild);
}
function addChatMessageError(msgError) {
  var elMessages = document.getElementById('chat-messages');
  var chatMessage = document.createElement('div');
  chatMessage.className='chat-message-error';
  var txt = document.createTextNode(msgError);
  chatMessage.appendChild(txt);
  elMessages.insertBefore(chatMessage, elMessages.firstChild);
}
function onStartGameClick(){
 fetch('/game/start-game', {
       method: 'POST',
       headers: {
         'Accept': 'application/json, text/plain, */*',
         'Content-Type': 'application/json'
       },
       body: JSON.stringify({numberOfPlayers: 2})
     }).then(res => res.json())
       .then(res => {
         console.log(res);
         if (res.status == 'ok')  {

         } else {
           addChatMessageError(res.errorMessage);
         }
       });
}
function onCancelClick(){
  fetch('/game/cancel-game', {
         method: 'POST',
         headers: {
           'Accept': 'application/json, text/plain, */*',
           'Content-Type': 'application/json'
         },
         body: JSON.stringify({})
       }).then(res => res.json())
         .then(res => {
           console.log(res);
           if (res.status == 'ok')  {

           } else {
             addChatMessageError(res.errorMessage);
           }
         });
}
function updateGameStatus(){
fetch('/game/get-status', {
      method: 'GET',
      headers: {
        'Accept': 'application/json, text/plain, */*'
      }
    }).then(res => res.json())
      .then(res => {
        console.log(res);
         var elStartGameBtn = document.getElementById('start-game-btn');
         var elCancelBtn = document.getElementById('cancel-btn');
         var elWaitingText = document.getElementById('waiting-text');
        if (res.type == 'init'){
          elWaitingText.style.display='none';
          elStartGameBtn.style.display='';
          elCancelBtn.style.display='none';
        } else if (res.type == 'running') {
         elWaitingText.style.display='none';
         elStartGameBtn.style.display='none';
         elCancelBtn.style.display='none';
        } else if (res.type == 'waitForGame'){
         elWaitingText.style.display='';
         elStartGameBtn.style.display='none';
         elCancelBtn.style.display='';
        }
      });
}
function loadChatMessages() {
fetch('/chat/get-messages?gameId=0', {
      method: 'GET',
      headers: {
        'Accept': 'application/json, text/plain, */*'
      }
    }).then(res => res.json())
      .then(res => {
        console.log(res);
        res.reverse().forEach((msg) => {
         addChatMessage(msg);
        });
      });
}



 const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket'
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/chat-messages/0', (chatMessage) => {
        console.log(chatMessage);
        chatMessage = JSON.parse(chatMessage.body);
        addChatMessage(chatMessage);
    });
    stompClient.subscribe('/user/game', (gameStatusUpdate) => {
            console.log(gameStatusUpdate);
            updateGameStatus();
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

