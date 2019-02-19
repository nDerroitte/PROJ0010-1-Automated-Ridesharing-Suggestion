function [finded_period,habit,y] = scenario(periods,reliability,variances,noise,min_point,offset)

day = 1440;
finded_period = 0;
filename = num2str( [periods reliability variances noise min_point offset]);
[x,y,offset] = createDates(periods,reliability,variances,noise,min_point,offset);
lag = round(length(y)/2);
acf1 = autocorr(y,lag);
acf1(1) = 0;
% figure
% plot(acf1);
% title(strcat('acf1',filename))
% save(strcat('y_',filename),'y');
% save(strcat('offset_',filename),'offset');
% save(strcat('acf1_',filename),'acf1');
acf2 = autocorr(acf1,lag);
acf2(1) = 0;
% save(strcat('acf2_',filename),'acf2');
% figure
% plot(acf2);
% title(strcat('acf2',filename))
[~, index] = sort(acf2,'descend');
candidate = index(1:floor(0.05 *length(index)));
candidate = round(candidate/day)*1440;
max_diff_period = 5;
count = 1;
for i=1 : length(candidate)
    if sum(ismember(candidate(i),finded_period)) == 0 && max_diff_period > 0
        finded_period(count) = candidate(i);
        count = count + 1;
        max_diff_period = max_diff_period - 1;   
    end
end

for i=1 : length(finded_period)
    habit{i} = gethabit(y,finded_period(i));
%     save(strcat('habit',num2str(i),'_',filename),'habit');
%     figure
%     plot(habit{i})
%     title(strcat('habit',num2str(i),'_',filename))
end
end

