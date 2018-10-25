maxAllowedAngularRate = 10.0*pi/180.0;
numSteps = 10000;
numRevs = 10;
europasPeriod = 3.551*24*60*60; %days to seconds
suggestedTimeStep = numRevs*europasPeriod/numSteps;

vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);

if ~exist('data','var')
    fprintf(1,'Loading Angular Rate Data'); tic;
    data = readTimestepLog('.\timestepLog.txt');
    fprintf(1,' (%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Angular Rate Data\n');
end

if ~exist('orbitData','var')
    fprintf(1,'Load Sim Log '); tic;
    orbitData = readSimLog('.\simLog.txt');
    fprintf(1,'(%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Data\n');
end

allIDs = unique(orbitData.id);

figure(1);
plot(data.dMag,data.angularRate*pi/180,'s'); hold('on');
myXLim = xlim();
plot(myXLim,maxAllowedAngularRate*[1,1],'--','Color','red'); hold('off');
xlim(myXLim);
grid('on');
xlabel('Distance Magnitude'); ylabel('Angular Rate (deg/sec)');


calculatedTimeSteps = maxAllowedAngularRate./data.angularRate;
figure(2);
ax(1) = subplot(2,1,1);
plot(calculatedTimeSteps,'s','Color','blue'); hold('on');
myXLim = xlim();
plot(myXLim,suggestedTimeStep*[1,1],'--','Color','red'); hold('off');
xlim(myXLim);
grid('on');
ylabel('Calculated Time Steps (s)');
ax(2) = subplot(2,1,2);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.aMagNorm(theseIndicies),'-'); hold('on');
end
hold('off');
xlabel('time step'); ylabel('a mag * r^2');
grid('on');
linkaxes(ax,'x');

timeStepsCruiseIndex = calculatedTimeSteps > suggestedTimeStep;
calculatedTimeSteps(timeStepsCruiseIndex) = suggestedTimeStep;
numSteps = suggestedTimeStep ./calculatedTimeSteps;
fprintf(1,'Estimate of Number of Steps: %5d\n',sum(numSteps));