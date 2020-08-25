## Must be grouped with ##

IHE requires certain actors to be grouped with specific other actors.
This means, that when inserting actors in application systems, there might be a need to add specific actors.

The sources for these groupings are the technical frameworks released on the [IHE website](https://www.ihe.net/resources/technical_frameworks/#top).

3LGM supports this by having these attributes stored in each actor. The tool will display error messages, when actors aren't grouped with the necessary actors.


## Example ##

In the following image we modeled a Patient Management System.

Starting from the bottom, first we will create the **application system** for patient management.
For the Patient Management System we will add the **Patient Identity Source** from the **Patient Identifier Cross-referencing profile (PIX)**

The **technical framework** states following groupings for the actors in the **Patient Identity Source** profile.

![mustbegroupedwith.PNG](https://bitbucket.org/repo/9L6rMz/images/3600768256-mustbegroupedwith.PNG)

So when we **add** the **Patient Identity Source**

![addingPIXSource.PNG](https://bitbucket.org/repo/9L6rMz/images/4292716758-addingPIXSource.PNG)

we get following **error message** on the **bottom of the 3LGM tool**.

![groupingError.PNG](https://bitbucket.org/repo/9L6rMz/images/2745019955-groupingError.PNG)

The error message states, that the actor **needs to be grouped with another actor**. To resolve this you can **right click on the error** and **add the missing actors** to the application system.

![groupingErrorResolve.PNG](https://bitbucket.org/repo/9L6rMz/images/3594253812-groupingErrorResolve.PNG)

This will add the missing actor to the application system. There might be a need for readjusting the position but the error message will be resolved by this.