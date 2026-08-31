## Must be grouped with ##

IHE requires certain actors to be grouped with specific other actors.
This means, that when inserting actors in application systems, there might be a need to add specific actors.

The sources for these groupings are the technical frameworks released on the [IHE website](https://www.ihe.net/resources/technical_frameworks/#top).

3LGM supports this by having these attributes stored in each actor. The tool will display error messages, when actors aren't grouped with the necessary actors.

The tool offers three options to resolve this error:

1. Option: Property dialog for error-correcting

2. Option: Remove inconsistent Element

3. Option: Connect


## Example ##

In the following image we modeled a Patient Management System.

Starting from the bottom, first we will create the **application system** for patient management.
For the Patient Management System we will add the **Patient Identity Source** from the **Patient Identifier Cross-referencing profile (PIX)**

![patientManagementSystem.PNG](/images/ihe/patientManagementSystem.PNG)

The **technical framework** states following groupings for the actors in the **Patient Identity Source** (PIX) profile.

![mustbegroupedwith.PNG](/images/ihe/mustbegroupedwith.PNG)

So when we **add** the **Patient Identity Source** from the Template Browser

![addingPIXSource.PNG](/images/ihe/addingPIXSource.PNG)

we get following **error message** on the **bottom of the 3LGM tool** (Consistency Check view).

![groupingError.PNG](/images/ihe/groupingError.PNG)

The error message states, that the actor **needs to be grouped with another actor**. By **right clicking on the error** you have three options to resolv this issue.

### 1. Option: Property dialog for error-correcting
![firstOption.PNG](/images/ihe/firstOption.PNG)

The first option will open the property dialogue window showing the vacant IHE Actors on the right.
![pDialogue.PNG](/images/ihe/pDialogue.PNG)

Doubleclick on the added Actor (Patient Identity Source) and it will open another window. Here go to the tab **Grouping** to find out which actors have to be grouped with the current one. 

![pDialogue2.PNG](/images/ihe/pDialogue2.PNG)

Now go back to the first window and select the vacant Actor(s), in this case the Time Client and add it to your application system by clicking on the **<** button.

![pDialogue3.PNG](/images/ihe/pDialogue3.PNG)

### 2. Option: Remove inconsistent Element
![secondOption.PNG](/images/ihe/secondOption.PNG)

This option will just remove the element, which generated the inconsistency error.

### 3. Option: Connect

![groupingErrorResolve.PNG](/images/ihe/groupingErrorResolve.PNG)

This will add the missing actor to the application system. There might be a need for readjusting the position but the error message will be resolved by this.