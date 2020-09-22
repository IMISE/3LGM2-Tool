# Analylsis of 3LGM² models #
The 3LGM² Tool offers a analysis function, which allows the user to answer given questions regarding selected elements.
Depending on the element type there are different questions offered by the analysis option:

***Application component:***

* On which physical data processing components is it installed?
* Which enterprise functions does this physical data processing component support?
* Which object types are stored?
* Which object types does this component send or receive?
* Which communication links does this component have?	

***Communication standard:***

* Where is this communication standard applied?

***Entity type:***

* Where is this object type communicated?
* Which interfaces can communicate this object type?
* Where is this object type stored?
* Which components belong to the data domain of this object type?

***ETDT-Combination:***

* Where is this ETDT-Combination transmitted, and which object types are communicated?

***ETMT-Combination:***

* Where is this ETNTCombination transmitted, and which object types are communicated?

***Event type:***

* Where are message types and document types connected to this event type transmitted, and which object types are communicated?

***Function:***

* Which application components and which physical data processing components are needed to achieve this enterprise function?
* Which application components are needed to achieve this enterprise function?

***Location:***

* Which physical data processing components are located at this location?

***Message type, Document type:***

* Where is this message or document type transmitted, and which object types are communicated?

***Organizational unit:***

* Which components belong to the scenario of this organizational unit?

***Physical data processing component:***

* Which database systems are installed on this physical data processing component, and which object types are stored in them?
* Which computer-based application components are installed on this physical data processing component?
* Which computer-based application components are installed on this physical data processing component, and which enterprise functions do they support?

***Software product:***

* On which application components it is installed?


## Accessing the analysis function ##
There are two main ways to access these functions.
The first option would be the **Analysis** option on the top menu bar.

![analysisMenuBar.PNG](https://bitbucket.org/repo/9L6rMz/images/817250633-analysisMenuBar.PNG)

### 1. Repository ###
This opens following window:

![analysisRepository.PNG](https://bitbucket.org/repo/9L6rMz/images/3171069154-analysisRepository.PNG)

Here you can select the wanted analysis. If you have selected an element beforehand, the analysis will be done for the selected element. Otherwise the Analysis will be done for every component.

### 2. Right Click ###
It is also possible to just right click on the component you want to analyze. This will offer you the analysis options available for this element type.

![analysisRightClick.PNG](https://bitbucket.org/repo/9L6rMz/images/175494577-analysisRightClick.PNG)