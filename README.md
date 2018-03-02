# StudyPied

> **pied** \ ˈpīd \  *adj.* • of two or more colors in blotches [(Marriam-Webster)](https://www.merriam-webster.com/dictionary/pied)

### What is StudyPied?
The modern student is commonly tasked with supplementing their learning via the collection and organization of information from a variety of technology based sources. StudyPied provides the end user a one-stop-shop GUI application to aid in the creation of such organized collections, herein refered to as "study guides" or simple "guides."

### Features
- Importing of guides from... 
  - [x] Google Drive
  - [ ] .Docx (Microsoft Word) files
  - [ ] .PDF files
- Guide aides such as...
  - [X] Categorized terms
  - [X] Query editing
  - [X] Searching for terms within large guides
  - [ ] Table interpretation
- Retrieving data matching term queries from...
  - [x] Quizlet.com
  - [ ] Supplied PDFs
  - [ ] Dictionary
- Exporting of guides to...
  - [X] Proprietary .GUIDE format
  - [ ] Quizlet.com
  - [ ] Google Drive
  - [X] .Docx format
  - [ ] .PDF format

### Utilized Tools and Technologies
StudyPied relies on various RESTful APIs and Java libraries to pull in and manage data from multiple digital and web sources. These include:
- **Kotlin** : Chosen for its interoperability with commonly available Java libraries and ease of data management
  - [Data classes](https://kotlinlang.org/docs/reference/data-classes.html) speed up development times by implementing essential utility functions by default
- **Apache APIs**
  - [Apache HttpComponents](http://hc.apache.org/) : Manages requests for APIs lacking Java bindings
  - [Apache POI](https://poi.apache.org/) : Manages reading and writing Microsoft Office formats
- **Google APIs**
  - [Google GSON](https://github.com/google/gson) : Deserializes JSON strings into equivalent Java data objects
  - [Google Custom Search JSON/Atom API](https://developers.google.com/custom-search/json-api/v1/overview) : Provides various advantages over traditional webservice-specific search requests
    - Utilizes Google's search algorithm to ensure the most pertinent results on the whole of web, or just specific sites
    - Takes care of inexact (mispelled, altnernative name, etc.)  queries
  - [Google Drive API](https://developers.google.com/drive/v3/web/about-sdk) : Used for import and export to Drive services
- **[Quizlet API](https://quizlet.com/api/)** : Retrieves user generated definitions from Quizlet.com
