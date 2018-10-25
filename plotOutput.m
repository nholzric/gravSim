dataFilePath = '.\simLog.txt';

vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);

if ~exist('orbitData','var')
    fprintf(1,'Load %s ',dataFilePath); tic;
    orbitData = readSimLog(dataFilePath);
    fprintf(1,'(%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Data\n');
end

allIDs = unique(orbitData.id);
figure(1);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.p(theseIndicies,1),orbitData.p(theseIndicies,2),'-'); hold('on');
end
hold('off');
grid('on');

figure(2);
subplot(2,1,1);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.time(theseIndicies),orbitData.aMag(theseIndicies,1),'-'); hold('on');
end
hold('off');
xlabel('time'); ylabel('a mag');
grid('on');
subplot(2,1,2);
for i = 1:size(allIDs,1)
    theseIndicies = orbitData.id == allIDs(i);
    plot(orbitData.time(theseIndicies),orbitData.aMag(theseIndicies,1).*(orbitData.pMag(theseIndicies).^2),'-'); hold('on');
end
hold('off');
xlabel('time'); ylabel('a mag * r^2');
grid('on');
