
document.addEventListener('DOMContentLoaded', () => {
    const chatIconBtn = document.getElementById('chatIconBtn');
    const navbarChatBtn = document.getElementById('navbarChatBtn');
    const chatWidgetModal = document.getElementById('chatWidgetModal');
    const minimizeChatBtn = document.getElementById('minimizeChatBtn');
    const closeChatBtn = document.getElementById('closeChatBtn');
    const sendBtn = document.getElementById('sendBtn');
    const messageInput = document.getElementById('messageInput');
    const chatMessages = document.getElementById('chatMessages');
    const typingIndicator = document.getElementById('typingIndicator');
    const chatNotificationBadge = document.getElementById('chatNotificationBadge');

    const toggleChatModal = () => {
        if (chatWidgetModal) {
            chatWidgetModal.classList.toggle('show');
            if (chatWidgetModal.classList.contains('show')) {
                chatWidgetModal.classList.add('fade-in');
                if (chatNotificationBadge) {
                    chatNotificationBadge.style.display = 'none';
                }
            } else {
                chatWidgetModal.classList.remove('fade-in');
                if (chatNotificationBadge) {
                    chatNotificationBadge.style.display = 'flex';
                }
            }
        }
    };

    if (navbarChatBtn) {
        navbarChatBtn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleChatModal();
        });
    }

    if (chatIconBtn) {
        chatIconBtn.addEventListener('click', toggleChatModal);
    }

    if (minimizeChatBtn) {
        minimizeChatBtn.addEventListener('click', () => {
            chatWidgetModal.classList.remove('show', 'fade-in');
            if (chatNotificationBadge) {
                chatNotificationBadge.style.display = 'flex';
            }
        });
    }

    if (closeChatBtn) {
        closeChatBtn.addEventListener('click', () => {
            chatWidgetModal.classList.remove('show', 'fade-in');
            if (chatNotificationBadge) {
                chatNotificationBadge.style.display = 'flex';
            }
        });
    }

    if (sendBtn) {
        sendBtn.addEventListener('click', async () => {
            const message = messageInput.value.trim();
            if (!message) return;

            const userMessage = `
                <div class="message-wrapper mb-2 flex-row-reverse">
                    <div class="flex-grow-1">
                        <div class="message-sender text-muted small mb-1 text-end">You</div>
                        <div class="message-bubble bg-primary text-white border rounded-3 p-2 shadow-sm">
                            <p class="mb-0 small">${message}</p>
                        </div>
                        <div class="message-time text-muted small mt-1 text-end">Just now</div>
                    </div>
                </div>
            `;
            chatMessages.insertAdjacentHTML('beforeend', userMessage);
            chatMessages.scrollTop = chatMessages.scrollHeight;
            messageInput.value = '';
            typingIndicator.classList.remove('d-none');

            try {
                const response = await fetch('/send-message', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': document.getElementById('csrfToken')?.value
                    },
                    body: JSON.stringify({ message }),
                });

                const contentType = response.headers.get('Content-Type');
                if (!contentType || !contentType.includes('application/json')) {
                    const text = await response.text();
                    console.error('Expected JSON, got:', text);
                    throw new Error('Server returned non-JSON response');
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const data = await response.json();
                typingIndicator.classList.add('d-none');

                const botMessage = `
                    <div class="message-wrapper mb-2">
                        <div class="d-flex align-items-start">
                            <img src="https://images.unsplash.com/photo-1494790108755-2616b612b786?w=32&h=32&fit=crop&crop=face"
                                 alt="Support" class="rounded-circle me-2" width="28" height="28">
                            <div class="flex-grow-1">
                                <div class="message-sender text-muted small mb-1">Sarah - Book Consultant</div>
                                <div class="message-bubble bg-light border rounded-3 p-2 shadow-sm">
                                    <p class="mb-0 small">${data.reply}</p>
                                </div>
                                <div class="message-time text-muted small mt-1">Just now</div>
                            </div>
                        </div>
                    </div>
                `;
                chatMessages.insertAdjacentHTML('beforeend', botMessage);
                chatMessages.scrollTop = chatMessages.scrollHeight;
            } catch (error) {
                typingIndicator.classList.add('d-none');
                const errorMessage = `
                    <div class="message-wrapper mb-2">
                        <div class="flex-grow-1">
                            <div class="message-bubble bg-danger text-white border rounded-3 p-2 shadow-sm">
                                <p class="mb-0 small">Error: ${error.message}</p>
                            </div>
                        </div>
                    </div>
                `;
                chatMessages.insertAdjacentHTML('beforeend', errorMessage);
                chatMessages.scrollTop = chatMessages.scrollHeight;
            }
        });
    }

    if (messageInput) {
        messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                sendBtn.click();
            }
        });
    }
});
