# JavaLearnBot

JavaLearnBot is a JavaFX-based educational chatbot application designed to assist users with programming-related questions.  
It integrates a Retrieval-Augmented Generation (RAG) pipeline to provide contextual, document-based answers using AI models.

---

## Features

- JavaFX graphical user interface
- AI-powered question answering
- Retrieval-Augmented Generation (RAG)
- Document ingestion and chunk indexing
- Query rewriting and semantic retrieval
- Answer generation using large language models (Open_AI)
- Chat history logging
- Evaluation and feedback storage
- Persistence throughout several boots
- Admin features

---

## Guide to Using JavaLearnBot

JavaLearnBot is designed to be ready for use upon launch. All required services, models, keys, are already setup and ready to be used without issue.
Written below are several use cases of our application, and a step-by-step guide on how to utilize it.


### 1. Starting the Application
- Launch the application normally after unzipping into IDE and waiting for dependencies to resolve.
- The Home Page will load automatically.
- Navigate to the **ChatBot** page to begin or **Begin Chat** button to start chatting with JavaLearnBot .

---

### 2. Asking a Question
- Enter your Java-related question in the input field at the top of the page.
- Click **Search**.
- JavaLearnBot will:
    - Rewrite the query for clarity
    - Retrieve relevant documents by comparing the vector embeddings between both
    - Generate an AI-assisted response using the rewrites, retrieved/reranked docs
- The answer will appear in the **Result** section, rewrites in the **Rewrites** section and retrieved docs in the **Retrieved Docs and Relevance Rank** section.

---
### 3. Exporting Questions and Answers
- After receiving a response add a score and label below the result and click **Export Answer and Question**.
- The application will save and send it to the evaluation table:
    - The original question
    - The generated answer
    - score and label selected
- The plan for this exported data in the future is to feed it back to the AI in order to obtain higher quality answers .

---

### 4. Logging History
- All interactions with JavaLearnBot are recorded in the **Logging History** table.
- Each log entry includes:
    - Timestamp
    - Question
    - Generated answer
- This allows users to review past queries without re-running them.
- These chat logs are then sent to the evaluation page in the chat logs page for admins to review and evaluate.

---

### 5. Clearing Input
- Click **Clear** to reset the input field and result display.
- This does not delete previous logs.

### 6. Documents Page

- Here users can search and filter for documents stored in the **Knowledge base**
- Users can filter by category or status or search the name of the document they want to view
- Once users find the document they want to view, the user can click it and it will be displayed in the document preview section
- For another method of viewing the document the user can double click the record in the table and a pop up with the full content of the document will be displayed
- Files added in the admin settings page will be displayed here immediately after process and ingestion

---

### 7. Evaluation Page

- Admins can evaluate past chat as correct or wrong along with scoring them
- Admins can click the evaluate button next to the chat log and it will open a dialog where admins can save their evaluations
- these evaluations will then be stored in the **Evaluations** table
- Below the evaluation table is a bar chart that automatically updates after an evaluation based on the score distribution of answers

---

### 8. Admin Page

- In this page admins can choose to add files to the knowledge base, delete existing files, and edit metadata
- Admins select a file, and its title will be automatically placed in the document title text field
- The user can change this name, select a category and write a description that fits the idea of the file
- Admins can then edit metadata and delete documents by clicking on the want they want to delete/edit


---







### Notes
- No manual configuration is required.
- Internet access is required for AI responses.
- The application is intended for educational use.
