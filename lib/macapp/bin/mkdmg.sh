#! /bin/zsh
#
# This script is from the message posted by Yvon Thoraval. More info at:
#
# http://lists.apple.com/archives/java-dev/2005/Aug/msg00192.html

PATH=/bin:/sbin:/usr/bin:/usr/sbin
SCRATCH=/tmp/.mkdmg.$$

# Output
#
croak()
{
    echo -n "\n$1"
}

# Clean up
#
halt()
{
    exit 1
}

# Check return status and bail out on error
#
chkerror()
{
    if [ $? -ne 0 ]
    then
        halt
    fi
}

main()
{

    # Check if exactly three command line arguments was specified
    #
    if [ $ARGC -ne 3 ]
    then
        echo "usage: mkdmg <file|directory> <destdirectory> <fileName>"
        exit 1
    fi

    # Check if the specified file/directory exists
    #
    if [ ! -e $1 ]
    then
        echo "*** $1 does not exist."
        exit 1
    fi

    # Check if the specified destdirectory exists
    #
    if [ ! -e $2 ]
    then
        echo "*** $2 does not exist."
        exit 1
    fi

    SRC=$1
    NAME=`basename $SRC`
    NAME="$NAME"
    ARCH=$3
    
    SCRATCH=$2

    echo -n "Using source $SRC"

    # Change directory to a scratch location
    #
    cd /tmp

    # Estimate how much space is needed to archive the file/folder
    #
    SIZE=`du -s -k $SRC | awk '{print $1}'`
    chkerror
    SIZE=`expr 5 + $SIZE / 1000`
    chkerror
    croak "Using $SIZE MB"

    # Create a disk image, redirecting all output to /dev/null
    #
    hdiutil create "$SCRATCH/$ARCH.dmg" -volname "$ARCH" -megabytes $SIZE -type SPARSE -fs HFS+ 2>/dev/null >/dev/null
    chkerror
    croak "$SCRATCH/$ARCH.dmg created"

    # Mount sparse image
    #
    hdid $SCRATCH/$ARCH.dmg.sparseimage 2>/dev/null >/dev/null
    chkerror
    croak "$SCRATCH/$ARCH.dmg.sparseimage attached"
   
    # Find out allocated device
    #
    DEV=`mount | grep "Volumes/$ARCH" | awk '{print $1}'`
    croak "Device in use is $DEV"

    # Use ditto to copy everything to the image, preserving resource forks
    #
    ditto -rsrcFork $SRC "/Volumes/$ARCH/$NAME" 2>/dev/null >/dev/null
    chkerror
    croak "Copied $SRC to /Volumes/$ARCH/$NAME"

    # Detach the disk image
    hdiutil detach $DEV 2>/dev/null >/dev/null
    chkerror
    croak "$DEV detached"

    # Compress the image (maximum compression)
    hdiutil convert "$SCRATCH/$ARCH.dmg.sparseimage" -format UDZO -o "$SCRATCH/$ARCH.dmg" -imagekey zlib-devel=9 2>/dev/null >/dev/null
    chkerror
    croak "Disk image successfully compressed"

    croak "$SCRATCH/$ARCH.dmg is ready"
	
	rm -rf "$SCRATCH/$ARCH.dmg.sparseimage"
	
    echo

    halt
}

main $1 $2 $3
